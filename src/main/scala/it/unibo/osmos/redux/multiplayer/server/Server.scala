package it.unibo.osmos.redux.multiplayer.server

import akka.actor.{ActorRef, PoisonPill}
import akka.pattern.ask
import akka.util.Timeout
import it.unibo.osmos.redux.ecs.entities.{EntityManager, PlayerCellEntity}
import it.unibo.osmos.redux.multiplayer.common.ActorSystemHolder
import it.unibo.osmos.redux.multiplayer.lobby.GameLobby
import it.unibo.osmos.redux.multiplayer.players.{BasePlayer, ReferablePlayer}
import it.unibo.osmos.redux.multiplayer.server.ServerActor._
import it.unibo.osmos.redux.mvc.controller.LevelInfo.LevelInfo
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

  implicit val who: String = "Server"
  implicit val timeout: Timeout = Timeout(5.seconds)

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
    * @return The username.
    */
  def getUsername: String

  /**
    * Gets the state of the server.
    * @return The server state.
    */
  def getState: ServerState

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
    * @param levelInfo The level info.
    */
  def startGame(levelContext: MultiPlayerLevelContext, levelInfo: LevelInfo): Unit

  /**
    * Signals all clients that the game have been stopped.
    * @param winner The username of the player who won, if not declared the winner is assumed to be the server.
    */
  def stopGame(winner: String = ""): Unit

  /**
    * Delivers a message to a specified client.
    * @param username The player username.
    * @param message The message.
    */
  def deliverMessage(username: String, message: Any): Unit

  /**
    * Broadcasts a message to all connected clients.
    * @param message The message to send.
    * @param clientsToExclude Specify clients to which the delivery must not be performed.
    */
  def broadcastMessage(message: Any, clientsToExclude: String*): Unit

  /**
    * Removes the player from the game.
    * @param username The player username.
    * @param notify If the player client needs to be notified.
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

    //the uuid of the cell entity that represents this client
    private var uuid: String = _
    //current state of the server
    private var status: ServerState = ServerState.Idle
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

    override def getState: ServerState = status

    override def kill(): Unit = {
      Logger.log("kill")

      status match {
        case ServerState.Game => broadcastMessage(GameEnded(false))
        case ServerState.Lobby => broadcastMessage(LobbyClosed)
        case _ => //do nothing is other states
      }
      if (lobby.nonEmpty) {
        lobby = None
      }
      if (ref.nonEmpty) {
        ref.get ! PoisonPill
        ref = None
      }

      status = ServerState.Dead
    }

    //COMMUNICATION

    override def deliverMessage(username: String, message: Any): Unit = {
      lobby.get.getPlayers.find(_.getUsername == username) match  {
        case Some(player) => player.getActorRef ! message
        case None => throw new IllegalArgumentException("Cannot deliver message to specific client if the username does not match any player.")
      }
    }

    override def broadcastMessage(message: Any, clientsToExclude: String*): Unit = {
      if (ref.isEmpty) throw new IllegalStateException("Unable to broadcast the message, server is not bind to an actor.")
      val usernameToExclude = clientsToExclude :+ this.username
      val actors = lobby.get.getPlayers.filterNot(p => usernameToExclude contains p.getUsername).map(_.getActorRef)
      actors.foreach(a => a ! message)
    }

    //GAME MANAGEMENT

    override def initGame(level: Level): Promise[Boolean] = {
      Logger.log("initGame")

      if (status != ServerState.Lobby) throw new UnsupportedOperationException(s"Cannot init the game because the server is in the state: $status")

      val promise = Promise[Boolean]()

      //assign player cells to lobby players
      val futures = setupClients(level)
      Future.sequence(futures) onComplete {
        case Success(_) => promise success true
        case Failure(t) => promise failure t
      }
      promise
    }

    override def startGame(levelContext: MultiPlayerLevelContext, levelInfo: LevelInfo): Unit = {
      Logger.log("startGame")

      if (status != ServerState.Lobby) throw new UnsupportedOperationException(s"Cannot start game because the server is in the state: $status")

      lobby.get.notifyGameStarted(levelContext)
      status = ServerState.Game
    }

    override def stopGame(winner: String = username): Unit = {
      Logger.log("stopGame")

      if (status != ServerState.Lobby && status != ServerState.Game) throw new UnsupportedOperationException(s"Cannot stop game because the server is in the state: $status")

      //if the server won, everyone else lost
      if (winner.equals(username)) {
        broadcastMessage(ServerActor.GameEnded(false))
      } else {
        deliverMessage(winner, GameEnded(true))
        broadcastMessage(GameEnded(false), winner)
      }

      status = ServerState.Lobby
    }

    override def notifyClientInputEvent(event: MouseEventWrapper): Unit = {
      Logger.log("notifyClientInputEvent")

      if (status != ServerState.Game) throw new UnsupportedOperationException(s"Cannot manage client input event because the server is in the state: $status")

      InputEventQueue enqueue event
    }

    override def removePlayerFromGame(username: String, notify: Boolean = false): Unit = {
      Logger.log("removePlayerFromGame")

      if (status != ServerState.Game) throw new UnsupportedOperationException(s"Cannot remove player from game because it's in the state: $status")

      //remove entity cell relative to the player that has left
      val player = getPlayerFromLobby(username)
      if (player.isEmpty) throw new IllegalArgumentException("Cannot remove player from game because it was not found.")

      //notify player that he has lost
      if (notify) player.get.getActorRef ! GameEnded(false)

      val playerEntity = EntityManager.filterEntities(classOf[PlayerCellEntity]).find(_.getUUID == player.get.getUUID)
      if (playerEntity.isEmpty) throw new IllegalArgumentException("Cannot remove player cell from game because it was not found.")

      EntityManager.delete(playerEntity.get)
      lobby.get.removePlayer(username)
    }

    //LOBBY MANAGEMENT

    override def createLobby(lobbyContext: LobbyContext): Unit = {
      Logger.log("createLobby")

      if (status != ServerState.Idle) throw new UnsupportedOperationException(s"Server cannot close lobby because it's in the state: $status")

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

      if (status != ServerState.Lobby) throw new UnsupportedOperationException(s"Server cannot close lobby because it's in the state: $status")

      broadcastMessage(LobbyClosed)
      lobby.get.notifyLobbyClosed()
      lobby = None

      status = ServerState.Idle
    }

    override def getLobbyPlayers: Seq[ReferablePlayer] = {

      if (status != ServerState.Lobby && status != ServerState.Game)
        throw new UnsupportedOperationException(s"Cannot because it's in the state: $status")

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

      if (availablePlayerCells.size <= 0) throw new IllegalStateException("Cannot setup clients if the level has no player cells.")

      //assign first available player cell to the server
      this.uuid = availablePlayerCells.head.get

      //update uuid of the server players
      val serverPlayer = getPlayerFromLobby(this.username)
      if (serverPlayer.isEmpty) throw new IllegalStateException("Cannot update server player uuid because the player was not found.")
      serverPlayer.get.setUUID(this.uuid)

      //get map shape and send to the clients along with the assigned uuid
      val mapShape = level.levelMap.mapShape

      //gather extra player cells
      val remainingPlayerCells = availablePlayerCells.tail
      val extraCellPlayers = remainingPlayerCells.slice(otherPlayers.size, remainingPlayerCells.size).map(_.get)
      //update level entities by removing the extra player cells
      level.entities = for (
        e <- level.entities
        if !(extraCellPlayers contains e.getUUID)
      ) yield e

      otherPlayers.map(Some(_)).zipAll(availablePlayerCells.tail, None, None).map {
        case (Some(p), Some(id)) => p.setUUID(id); Some(p.getActorRef ? GameStarted(id, mapShape))
        case (None, _) => None
        case _ => throw new IllegalStateException("Not enough player cells for all the clients.")
      }.filter(_.nonEmpty).map(_.get)
    }

    private def getPlayerFromLobby(username: String): Option[ReferablePlayer] = {
      Logger.log("getPlayerFromLobby")

      lobby.get.getPlayers.find(_.getUsername == username)
    }
  }
}



