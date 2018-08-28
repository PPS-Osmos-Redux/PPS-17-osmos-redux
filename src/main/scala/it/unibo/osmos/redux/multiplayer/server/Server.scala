package it.unibo.osmos.redux.multiplayer.server

import akka.actor.ActorRef
import akka.pattern.{ask, gracefulStop}
import akka.util.Timeout
import it.unibo.osmos.redux.ecs.entities.{EntityManager, PlayerCellEntity}
import it.unibo.osmos.redux.multiplayer.common.ActorSystemHolder
import it.unibo.osmos.redux.multiplayer.lobby.ServerLobby
import it.unibo.osmos.redux.multiplayer.players.{BasicPlayer, ReferablePlayer}
import it.unibo.osmos.redux.multiplayer.server.ServerActor._
import it.unibo.osmos.redux.mvc.model.Level
import it.unibo.osmos.redux.mvc.view.components.multiplayer.User
import it.unibo.osmos.redux.mvc.view.context.{LobbyContext, MultiPlayerLevelContext}
import it.unibo.osmos.redux.mvc.view.events.MouseEventWrapper
import it.unibo.osmos.redux.utils.{InputEventQueue, Logger}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future, Promise}
import scala.util.{Failure, Success}

trait Server {

  implicit val timeout: Timeout = Timeout(5.minutes) //TODO: change to 5 sec

  /**
    * Binds this instance with the input ActorRef.
    * @param actorRef The ActorRef.
    */
  def bind(actorRef: ActorRef): Unit

  /**
    * Gets the uuid of the cell entity that represents this client.
    * @return The uuid.
    */
  def getUUID: String

  /**
    * Gets the username of the player representing the server.
    * @return The username
    */
  def getUsername: String

  /**
    * Kills this instance.
    */
  def kill(): Unit

  /**
    * Signals all clients that the game needs to be started and checks that they all reply.
    * @return Promise that completes with true if all clients replied before the timeout; otherwise false.
    */
  def initGame(level: Level): Promise[Boolean]

  /**
    * Starts the game by notifying the interface and passing the level context to use.
    * @param levelContext The level context.
    */
  def startGame(levelContext: MultiPlayerLevelContext): Unit

  /**
    * Signals all clients that the game have been stopped.
    */
  def stopGame(): Unit

  /**
    * Delivers a message to a specified client
    * @param username The player username
    * @param message The message
    */
  def deliverMessage(username: String, message: Any): Unit

  /**
    * Broadcasts a message to all connected clients.
    * @param message The message to send.
    * @param clientsToExclude Specify clients to which the delivery must not be performed
    */
  def broadcastMessage(message: Any, clientsToExclude: String*): Unit

  /**
    * Removes the player from the game.
    * @param username The player username.
    * @param notify If the player client needs to be notified
    */
  def removePlayerFromGame(username: String, notify: Boolean = false)

  /**
    * Notifies server about new client input event.
    * @param event The event.
    */
  def notifyClientInputEvent(event: MouseEventWrapper): Unit

  /**
    * Creates a lobby and enters it, all further requests to enter it are handled.
    * @param lobbyContext The lobby context.
    */
  def createLobby(lobbyContext: LobbyContext): Unit

  /**
    * Closes the lobby, all further requests to enter it are ignored.
    */
  def closeLobby(): Unit

  /**
    * Gets the lobby players.
    * @return All players in the lobby.
    */
  def getLobbyPlayers: Seq[ReferablePlayer]

  /**
    * Removes a player from the lobby.
    * @param username The username of the player.
    */
  def removePlayerFromLobby(username: String): Unit

  /**
    * Adds a player to the lobby.
    * @param actorRef The actor ref.
    * @param player The player to add.
    * @return True, if the player is added correctly; false if the lobby is full.
    */
  def addPlayerToLobby(actorRef: ActorRef, player: BasicPlayer): Boolean
}

object Server {
  def apply(username: String = ""): Server = ServerImpl(username)

  final case class ServerImpl(private var username: String) extends Server {

    implicit val who: String = "Server"

    //the uuid of the cell entity that represents this client
    private var uuid: String = _

    //the current lobby
    private var lobby: Option[ServerLobby] = None
    //the actor ref used to send and receive
    private var ref: Option[ActorRef] = None

    //MAIN METHODS

    override def bind(actorRef: ActorRef): Unit = ref = {
      Logger.log("bind")

      Some(actorRef)
    }

    override def getUUID: String = uuid

    override def getUsername: String = username

    override def kill(): Unit = {
      Logger.log("kill")

      if (ref.nonEmpty) {
        //attempt to wait all mailbox messages delivery
        try {
          val stopped: Future[Boolean] = gracefulStop(ref.get, 4 seconds)
          Await.result(stopped, 5 seconds)
        } catch {
          case e: Throwable => e.printStackTrace()
        } finally {
          ActorSystemHolder.stopActor(ref.get)
        }
        ref = None
      }
      if (lobby.nonEmpty) {
        lobby.get.clear()
      }
    }

    //COMMUNICATION

    override def deliverMessage(username: String, message: Any): Unit = {
      Logger.log("deliverMessage")

      lobby.get.getPlayers.find(_.username == username) match  {
        case Some(player) => player.actorRef ! message
        case None => throw new IllegalArgumentException("Cannot deliver message to specific client if the username does not match any player.")
      }
    }

    override def broadcastMessage(message: Any, clientsToExclude: String*): Unit = {
      Logger.log("broadcastMessage")

      if (ref.isEmpty) throw new IllegalStateException("Unable to broadcast the message, server is not bind to an actor.")
      val usernameToExclude = clientsToExclude :+ this.username
      val actors = lobby.get.getPlayers.filterNot(p => usernameToExclude contains p.username).map(_.actorRef)
      actors.foreach(a => a ! message)
    }

    //GAME MANAGEMENT

    override def initGame(level: Level): Promise[Boolean] = {
      Logger.log("initGame")

      val promise = Promise[Boolean]()

      //assign player cells to lobby players
      val futures = assignCellsToPlayers(level)
      Future.sequence(futures) onComplete {
        case Success(_) => promise success true
        case Failure(_) => promise failure _
      }
      promise
    }

    override def startGame(levelContext: MultiPlayerLevelContext): Unit = {
      Logger.log("startGame")

      lobby.get.startGame(levelContext)
    }

    override def stopGame(): Unit = {
      Logger.log("stopGame")

      broadcastMessage(ServerActor.GameEnded(false))
      kill()
    }

    override def notifyClientInputEvent(event: MouseEventWrapper): Unit = {
      Logger.log("notifyClientInputEvent")

      InputEventQueue enqueue event
    }

    override def removePlayerFromGame(username: String, notify: Boolean = false): Unit = {
      Logger.log("removePlayerFromGame")

      //remove entity cell relative to the player that has left
      val player = getPlayerFromLobby(username)
      if (player.isEmpty) throw new IllegalArgumentException("Cannot remove player from game because it was not found.")

      //notify player that he has lost
      if (notify) player.get.actorRef ! GameEnded(false)

      val playerEntity = EntityManager.filterEntities(classOf[PlayerCellEntity]).find(_.getUUID == player.get.getUUID)
      if (player.isEmpty) throw new IllegalArgumentException("Cannot remove player cell from game because it was not found.")

      EntityManager.delete(playerEntity.get)
      lobby.get.removePlayer(username)
    }

    //LOBBY MANAGEMENT

    override def createLobby(lobbyContext: LobbyContext): Unit = {
      Logger.log("createLobby")

      lobby = Some(ServerLobby(lobbyContext))
      val address = ActorSystemHolder.systemAddress
      //add the server itself
      val serverPlayer = BasicPlayer(username, address.host.getOrElse("0.0.0.0"), address.port.getOrElse(0))
      //let interface to show immediately the server player
      lobbyContext.users = Seq(new User(serverPlayer, true))
      //add himself to the lobby
      lobby.get.addPlayer(new ReferablePlayer(serverPlayer, ref.get))
    }

    override def closeLobby(): Unit = {
      Logger.log("createLobby")

      broadcastMessage(LobbyClosed)
      lobby = None
    }

    def broadcastWithReply(message: Any, clientsToExclude: String*): Seq[Future[Any]] = {
      Logger.log("broadcastWithReply")

      if (ref.isEmpty) throw new IllegalStateException("Unable to broadcast the message, server is not bind to an actor.")
      val usernameToExclude = clientsToExclude :+ this.username
      val actors = lobby.get.getPlayers.filterNot(p => usernameToExclude contains p.username).map(_.actorRef)
      actors.map(a => a ? message)
    }

    override def getLobbyPlayers: Seq[ReferablePlayer] = {
      Logger.log("getLobbyPlayers")

      lobby.get.getPlayers
    }

    override def addPlayerToLobby(actorRef: ActorRef, player: BasicPlayer): Boolean = {
      Logger.log("addPlayerToLobby")

      if (!lobby.get.isFull) {
        val newPlayer = ReferablePlayer(player.username, player.address, player.port, actorRef)
        broadcastMessage(PlayerEnteredLobby(newPlayer.toBasicPlayer))
        lobby.get.addPlayer(newPlayer)
        true
      } else false
    }

    override def removePlayerFromLobby(username: String): Unit = {
      Logger.log("removePlayerFromLobby")

      lobby.get.removePlayer(username)
      broadcastMessage(PlayerLeftLobby(username))
    }

    //HELPER METHODS

    private def assignCellsToPlayers(level: Level): Seq[Future[Any]] = {
      Logger.log("assignCellsToPlayers")

      val availablePlayerCells = level.entities.filter(_.isInstanceOf[PlayerCellEntity]).map(p => Some(p.getUUID))
      val otherPlayers = lobby.get.getPlayers.filterNot(_.username == this.username)

      if (availablePlayerCells.size <= 0) throw new IllegalStateException()

      //assign first available player cell to the server
      this.uuid = availablePlayerCells.head.get

      otherPlayers.zipAll(availablePlayerCells.tail, null, None).map {
        case (p, Some(id)) =>  p.setUUID(id); p.actorRef ? GameStarted(id)
        case (_, None) => throw new IllegalStateException("Not enough player cells for all the clients")
      }
    }

    private def getPlayerFromLobby(username: String): Option[ReferablePlayer] = {
      Logger.log("getPlayerFromLobby")

      lobby.get.getPlayers.find(_.username == username)
    }
  }
}



