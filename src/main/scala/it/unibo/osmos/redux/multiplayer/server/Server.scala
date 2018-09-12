package it.unibo.osmos.redux.multiplayer.server

import akka.actor.{ActorRef, PoisonPill}
import akka.pattern.ask
import akka.util.Timeout
import it.unibo.osmos.redux.ecs.entities.{EntityManager, PlayerCellEntity}
import it.unibo.osmos.redux.multiplayer.common.{ActorSystemHolder, NetworkUtils}
import it.unibo.osmos.redux.multiplayer.lobby.GameLobby
import it.unibo.osmos.redux.multiplayer.players.{BasePlayer, ReferablePlayer}
import it.unibo.osmos.redux.multiplayer.server.ServerActor._
import it.unibo.osmos.redux.mvc.controller.levels.structure.{Level, LevelInfo}
import it.unibo.osmos.redux.mvc.view.components.multiplayer.User
import it.unibo.osmos.redux.mvc.view.context.{LobbyContext, MultiPlayerLevelContext}
import it.unibo.osmos.redux.mvc.view.events.MouseEventWrapper
import it.unibo.osmos.redux.utils.InputEventQueue

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}

/** Trait representing a Server */
trait Server {

  type ServerState = ServerState.Value

  implicit val who: String = "Server"
  implicit val timeout: Timeout = Timeout(5.seconds)

  /** Binds this instance with the input ActorRef.
    *
    * @param actorRef The ActorRef.
    */
  def bind(actorRef: ActorRef): Unit

  /** Gets the uuid of the cell entity that represents this client.
    *
    * @return The uuid.
    */
  def getUUID: String

  /** Gets the username of the player representing the server.
    *
    * @return The username.
    */
  def getUsername: String

  /** Gets the state of the server.
    *
    * @return The server state.
    */
  def getState: ServerState

  /** Kills this instance. */
  def kill(): Unit

  /** Signals all clients that the game needs to be started and checks that they all reply.
    *
    * @param level The level.
    * @return Promise that completes with true if all clients replied before the timeout; otherwise false.
    */
  def initGame(level: Level): Promise[Boolean]

  /** Starts the game by notifying the interface and passing the level context to use.
    *
    * @param levelContext The level context.
    * @param levelInfo    The level info.
    */
  def startGame(levelContext: MultiPlayerLevelContext, levelInfo: LevelInfo): Unit

  /** Signals all clients that the game have been stopped.
    *
    * @param winner The username of the player who won, if not declared the winner is assumed to be the server.
    */
  def stopGame(winner: String = ""): Unit

  /** Delivers a message to a specified client.
    *
    * @param username The player username.
    * @param message  The message.
    */
  def deliverMessage(username: String, message: Any): Unit

  /** Broadcasts a message to all connected clients.
    *
    * @param message          The message to send.
    * @param clientsToExclude Specify clients to which the delivery must not be performed.
    */
  def broadcastMessage(message: Any, clientsToExclude: String*): Unit

  /** Removes the player from the game.
    *
    * @param username The player username.
    * @param notify   If the player client needs to be notified.
    */
  def removePlayerFromGame(username: String, notify: Boolean = false)

  /** Notifies server about new client input event.
    *
    * @param event The event.
    */
  def notifyClientInputEvent(event: MouseEventWrapper): Unit

  /** Creates a lobby and enters it, all further requests to enter it are handled.
    *
    * @param lobbyContext The lobby context.
    */
  def createLobby(lobbyContext: LobbyContext): Unit

  /** Closes the lobby, all further requests to enter it are ignored. */
  def closeLobby(): Unit

  /** Gets the lobby players.
    *
    * @return All players in the lobby.
    */
  def getLobbyPlayers: Seq[ReferablePlayer]

  /** Removes a player from the lobby.
    *
    * @param username The username of the player.
    */
  def removePlayerFromLobby(username: String): Unit

  /** Adds a player to the lobby.
    *
    * @param actorRef The actor ref.
    * @param player   The player to add.
    * @return True, if the player is added correctly; false if the lobby is full.
    */
  def addPlayerToLobby(actorRef: ActorRef, player: BasePlayer): Boolean
}

object Server {

  /** The server username in the lobby.
    *
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

    override def bind(actorRef: ActorRef): Unit = ref = Some(actorRef)

    override def getUUID: String = uuid

    override def getUsername: String = username

    override def getState: ServerState = status

    override def kill(): Unit = {
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

    override def broadcastMessage(message: Any, clientsToExclude: String*): Unit = {
      if (ref.isEmpty) throw new IllegalStateException("Unable to broadcast the message, server is not bind to an actor.")

      val usernameToExclude = clientsToExclude :+ this.username
      val actors = lobby.get.getPlayers.filterNot(p => (usernameToExclude contains p.getUsername) || !p.isAlive).map(_.getActorRef)
      actors.foreach(a => a ! message)
    }

    override def initGame(level: Level): Promise[Boolean] = {
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

    //GAME MANAGEMENT

    private def setupClients(level: Level): Seq[Future[Any]] = {
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

      //prepare ask requests for all clients and return futures
      otherPlayers.map(Some(_)).zipAll(availablePlayerCells.tail, None, None).map {
        case (Some(p), Some(id)) => p.setUUID(id); Some(p.getActorRef ? GameStarted(id, level.levelInfo, mapShape))
        case (None, _) => None
        case _ => throw new IllegalStateException("Not enough player cells for all the clients.")
      }.filter(_.nonEmpty).map(_.get)
    }

    override def startGame(levelContext: MultiPlayerLevelContext, levelInfo: LevelInfo): Unit = {
      if (status != ServerState.Lobby) throw new UnsupportedOperationException(s"Cannot start game because the server is in the state: $status")

      lobby match {
        case Some(gameLobby) =>
          gameLobby.notifyGameStarted(levelContext, levelInfo)
          status = ServerState.Game
        case _ => throw new IllegalArgumentException("Unable to start the game if the lobby is not defined.")
      }
    }

    override def stopGame(winner: String = username): Unit = {
      if (status != ServerState.Lobby && status != ServerState.Game) throw new UnsupportedOperationException(s"Cannot stop game because the server is in the state: $status")

      //if the server won, everyone else lost
      if (winner.equals(username)) {
        broadcastMessage(ServerActor.GameEnded(false))
      } else {
        deliverMessage(winner, GameEnded(true))
        broadcastMessage(GameEnded(false), winner)
      }
      //reset lobby death status
      resetLobbyPlayersDeathStatus()
      //change current server status
      status = ServerState.Lobby
    }

    override def deliverMessage(username: String, message: Any): Unit = {
      if (lobby.isEmpty) throw new UnsupportedOperationException("Cannot deliver message to specific client if the lobby is not defined.")

      lobby.get.getPlayers.find(_.getUsername == username) match {
        case Some(player) => player.getActorRef ! message
        case None => throw new IllegalArgumentException("Cannot deliver message to specific client if the username does not match any player.")
      }
    }

    private def resetLobbyPlayersDeathStatus(): Unit = lobby match {
      case Some(gameLobby) => gameLobby.getPlayers.foreach(_ setLiveness true)
      case _ => throw new UnsupportedOperationException("Cannot reset lobby players death status if the lobby is not defined.")
    }

    //LOBBY MANAGEMENT

    override def notifyClientInputEvent(event: MouseEventWrapper): Unit = {
      if (status != ServerState.Game) throw new UnsupportedOperationException(s"Cannot manage client input event because the server is in the state: $status")

      InputEventQueue enqueue event
    }

    override def removePlayerFromGame(username: String, notify: Boolean = false): Unit = {
      if (status != ServerState.Game) throw new UnsupportedOperationException(s"Cannot remove player from game because it's in the state: $status")

      //detect if the dead player is the server itself
      val isServer = username == this.username

      //remove entity cell relative to the player that has left
      val player = getPlayerFromLobby(username)
      if (player.isEmpty) throw new IllegalArgumentException("Cannot remove player from game because it was not found.")

      //notify player that he has lost (just clients)
      if (notify && !isServer) player.get.getActorRef ! GameEnded(false)

      //get the player entity
      val playerEntity = EntityManager.filterEntities(classOf[PlayerCellEntity]).find(_.getUUID == player.get.getUUID)
      if (playerEntity.isEmpty) throw new IllegalArgumentException("Cannot remove player cell from game because it was not found.")

      EntityManager.delete(playerEntity.get)
      //set the player as dead only if it's not the server itself
      setLobbyPlayerAsDead(username)
    }

    private def getPlayerFromLobby(username: String): Option[ReferablePlayer] = lobby match {
      case Some(gameLobby) => gameLobby.getPlayers.find(_.getUsername == username)
      case _ => throw new UnsupportedOperationException("Cannot get lobby player if the lobby is not defined.")
    }

    private def setLobbyPlayerAsDead(username: String): Unit = lobby match {
      case Some(gameLobby) => gameLobby.getPlayers.filter(_.getUsername == username).foreach(_ setLiveness false)
      case _ => throw new UnsupportedOperationException("Cannot set lobby player as dead if the lobby is not defined.")
    }

    override def createLobby(lobbyContext: LobbyContext): Unit = {
      if (status != ServerState.Idle) throw new UnsupportedOperationException(s"Server cannot close lobby because it's in the state: $status")

      //retrieve actor system address
      val address = ActorSystemHolder.systemAddress
      //abort if something is wrong with the system
      if (!NetworkUtils.validatePort(address.port.getOrElse(-1)) ||
        !NetworkUtils.validateIPV4Address(address.host.getOrElse(""))) {
        throw new IllegalStateException("Server cannot create the lobby because the actor system gave invalid address or port.")
      }

      //Create the lobby
      lobby = Some(GameLobby(lobbyContext))
      //add the server itself
      val serverPlayer = BasePlayer(username, address.host.get, address.port.get)
      //let interface to show immediately the server player
      lobbyContext.users = Seq(new User(serverPlayer, true))
      //add himself to the lobby
      lobby.get.addPlayer(new ReferablePlayer(serverPlayer, ref.get))

      status = ServerState.Lobby
    }

    //HELPER METHODS

    override def closeLobby(): Unit = {
      if (status != ServerState.Lobby) throw new UnsupportedOperationException(s"Server cannot close lobby because it's in the state: $status")
      if (lobby.isEmpty) throw new UnsupportedOperationException(s"Cannot close lobby if it's not defined.")

      //broadcast that the lobby is closed
      broadcastMessage(LobbyClosed)
      //let view change scene
      lobby.get.notifyLobbyClosed()
      //clear lobby variable
      lobby = None

      status = ServerState.Idle
    }

    override def getLobbyPlayers: Seq[ReferablePlayer] = {
      (status, lobby) match {
        case (ServerState.Lobby | ServerState.Game, Some(gameLobby)) => gameLobby.getPlayers
        case (_, None) => throw new UnsupportedOperationException(s"Cannot get lobby players because the lobby is not defined.")
        case _ => throw new UnsupportedOperationException(s"Cannot because it's in the state: $status")
      }
    }

    override def addPlayerToLobby(actorRef: ActorRef, player: BasePlayer): Boolean = {
      (status, lobby) match {
        case (ServerState.Lobby, Some(gameLobby)) =>
          if (!gameLobby.isFull) {
            val newPlayer = new ReferablePlayer(player.getUsername, player.getAddress, player.getPort, actorRef)
            //tell everyone a new player entered the lobby
            broadcastMessage(PlayerEnteredLobby(newPlayer.toBasicPlayer))
            //add the player to the lobby
            gameLobby.addPlayer(newPlayer)
            true
          } else false
        case _ => throw new IllegalStateException("Unable to add player to lobby because the lobby is undefined")
      }
    }

    override def removePlayerFromLobby(username: String): Unit = {
      (status, lobby) match {
        case (ServerState.Lobby | ServerState.Game, Some(gameLobby)) =>
          //effectively remove player from lobby
          gameLobby.removePlayer(username)
          //tell everyone the player left the game
          broadcastMessage(PlayerLeftLobby(username))
        case _ => throw new IllegalStateException("Unable to remove player to lobby because the lobby is undefined")
      }
    }
  }

}



