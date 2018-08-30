package it.unibo.osmos.redux.multiplayer.server

import akka.actor.{ActorRef, PoisonPill}
import akka.pattern.ask
import akka.util.Timeout
import it.unibo.osmos.redux.ecs.entities.{EntityManager, PlayerCellEntity}
import it.unibo.osmos.redux.multiplayer.common.ActorSystemHolder
import it.unibo.osmos.redux.multiplayer.lobby.GameLobby
import it.unibo.osmos.redux.multiplayer.players.{BasePlayer, ReferablePlayer}
import it.unibo.osmos.redux.multiplayer.server.ServerActor._
import it.unibo.osmos.redux.mvc.model.Level
import it.unibo.osmos.redux.mvc.view.components.multiplayer.User
import it.unibo.osmos.redux.mvc.view.context.{LobbyContext, MultiPlayerLevelContext}
import it.unibo.osmos.redux.mvc.view.events.MouseEventWrapper
import it.unibo.osmos.redux.utils.{InputEventQueue, Logger}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}

trait Server {

  type ServerState = ServerState.Value
  implicit val timeout: Timeout = Timeout(5.seconds) //TODO: change to 5 sec

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
  def addPlayerToLobby(actorRef: ActorRef, player: BasePlayer): Boolean
}

object Server {

  /**
    * The server username in the lobby.
    * @param username The username
    * @return A ServerImpl instance
    */
  def apply(username: String): Server = ServerImpl(username)

  final case class ServerImpl(private var username: String) extends Server {

    implicit val who: String = "Server"

    //current state of the server
    private var status: ServerState = ServerState.Idle
    //the uuid of the cell entity that represents this client
    private var uuid: String = _

    //the current lobby
    private var lobby: Option[GameLobby[ReferablePlayer]] = None
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
        ref.get ! PoisonPill
        ref = None
      }
      if (lobby.nonEmpty) {
        lobby = None
      }

      status = ServerState.Dead
    }

    //COMMUNICATION

    override def deliverMessage(username: String, message: Any): Unit = {
      Logger.log("deliverMessage")

      lobby.get.getPlayers.find(_.getUsername == username) match  {
        case Some(player) => player.getActorRef ! message
        case None => throw new IllegalArgumentException("Cannot deliver message to specific client if the username does not match any player.")
      }
    }

    override def broadcastMessage(message: Any, clientsToExclude: String*): Unit = {
      Logger.log("broadcastMessage")

      if (ref.isEmpty) throw new IllegalStateException("Unable to broadcast the message, server is not bind to an actor.")
      val usernameToExclude = clientsToExclude :+ this.username
      val actors = lobby.get.getPlayers.filterNot(p => usernameToExclude contains p.getUsername).map(_.getActorRef)
      actors.foreach(a => a ! message)
    }

    //GAME MANAGEMENT

    override def initGame(level: Level): Promise[Boolean] = {
      Logger.log("initGame")

      val promise = Promise[Boolean]()

      //assign player cells to lobby players
      val futures = setupClients(level)
      Future.sequence(futures) onComplete {
        case Success(_) => promise success true
        case Failure(t) => promise failure t
      }
      promise
    }

    override def startGame(levelContext: MultiPlayerLevelContext): Unit = {
      Logger.log("startGame")

      lobby.get.notifyGameStarted(levelContext, asServer = true)
      status = ServerState.Game
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
      if (notify) player.get.getActorRef ! GameEnded(false)

      val playerEntity = EntityManager.filterEntities(classOf[PlayerCellEntity]).find(_.getUUID == player.get.getUUID)
      if (player.isEmpty) throw new IllegalArgumentException("Cannot remove player cell from game because it was not found.")

      EntityManager.delete(playerEntity.get)
      lobby.get.removePlayer(username)
    }

    //LOBBY MANAGEMENT

    override def createLobby(lobbyContext: LobbyContext): Unit = {
      Logger.log("createLobby")

      if (lobby.nonEmpty) throw new IllegalStateException("Server does not have an active lobby, unable to close it.")
      if (status != ServerState.Idle) throw new IllegalStateException(s"Server cannot close lobby because it's in the state: $status")

      lobby = Some(GameLobby(lobbyContext))
      val address = ActorSystemHolder.systemAddress
      //add the server itself
      val serverPlayer = BasePlayer(username, address.host.getOrElse("0.0.0.0"), address.port.getOrElse(0))
      //let interface to show immediately the server player
      lobbyContext.users = Seq(new User(serverPlayer, true))
      //add himself to the lobby
      lobby.get.addPlayer(new ReferablePlayer(serverPlayer, ref.get))

      status = ServerState.Lobby
    }

    override def closeLobby(): Unit = {
      Logger.log("closeLobby")

      if (lobby.isEmpty) throw new IllegalStateException("Server does not have an active lobby, unable to close it.")
      if (status != ServerState.Lobby) throw new IllegalStateException(s"Server cannot close lobby because it's in the state: $status")

      broadcastMessage(LobbyClosed)
      lobby.get.notifyLobbyClosed() //signal interface to change scene
      lobby = None

      status = ServerState.Idle
    }

    override def getLobbyPlayers: Seq[ReferablePlayer] = {
      Logger.log("getLobbyPlayers")

      lobby.get.getPlayers
    }

    override def addPlayerToLobby(actorRef: ActorRef, player: BasePlayer): Boolean = {
      Logger.log(s"addPlayerToLobby -> ${player.getUsername}, $actorRef")

      status match {
        case ServerState.Lobby =>
          if (!lobby.get.isFull) {
            val newPlayer = new ReferablePlayer(player.getUsername, player.getAddress, player.getPort, actorRef)
            broadcastMessage(PlayerEnteredLobby(newPlayer.toBasicPlayer))
            lobby.get.addPlayer(newPlayer)
            true
          } else false
        case _ =>
          Logger.log(s"Unable to add player to lobby because the server status is $status")
          false
      }
    }

    override def removePlayerFromLobby(username: String): Unit = {
      Logger.log("removePlayerFromLobby")

      status match {
        case ServerState.Lobby =>
          lobby.get.removePlayer(username)
          broadcastMessage(PlayerLeftLobby(username))
        case _ =>
      }
    }

    //HELPER METHODS

    private def setupClients(level: Level): Seq[Future[Any]] = {
      Logger.log("assignCellsToPlayers")

      val availablePlayerCells = level.entities.filter(_.isInstanceOf[PlayerCellEntity]).map(p => Some(p.getUUID))
      val otherPlayers = lobby.get.getPlayers.filterNot(_.getUsername == this.username)

      if (availablePlayerCells.size <= 0) throw new IllegalStateException()

      //assign first available player cell to the server
      this.uuid = availablePlayerCells.head.get

      //get map shape and send to the clients along with the assigned uuid
      val mapShape = level.levelMap.mapShape

      otherPlayers.zipAll(availablePlayerCells.tail, null, None).map {
        case (p, Some(id)) =>  p.setUUID(id); p.getActorRef ? GameStarted(id, mapShape)
        case (_, None) => throw new IllegalStateException("Not enough player cells for all the clients")
      }
    }

    private def getPlayerFromLobby(username: String): Option[ReferablePlayer] = {
      Logger.log("getPlayerFromLobby")

      lobby.get.getPlayers.find(_.getUsername == username)
    }
  }
}



