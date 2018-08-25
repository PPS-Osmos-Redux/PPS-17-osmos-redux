package it.unibo.osmos.redux.multiplayer.server

import java.util.UUID

import akka.actor.{ActorRef, PoisonPill}
import akka.util.Timeout
import akka.pattern.ask
import it.unibo.osmos.redux.ecs.entities.PlayerCellEntity
import it.unibo.osmos.redux.multiplayer.common.ActorSystemHolder
import it.unibo.osmos.redux.multiplayer.lobby.ServerLobby
import it.unibo.osmos.redux.multiplayer.players.{BasicPlayer, PlayerInfo, ReferablePlayer}
import it.unibo.osmos.redux.multiplayer.server.ServerActor.{PlayerEnteredLobby, PlayerLeftLobby}
import it.unibo.osmos.redux.mvc.model.Level
import it.unibo.osmos.redux.mvc.view.context.{LevelContext, LobbyContext}
import it.unibo.osmos.redux.mvc.view.drawables.DrawableWrapper
import it.unibo.osmos.redux.mvc.view.events.MouseEventWrapper
import it.unibo.osmos.redux.utils.InputEventQueue

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}
import scala.concurrent.duration._
import scala.util.{Failure, Success}

trait Server {

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
  def getUUID: UUID

  /**
    * Sets the uuid of the cell entity that represents this client.
    * @param uuid The uuid.
    */
  def setUUID(uuid: UUID): Unit

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
  def getLobbyPlayers: Seq[BasicPlayer]

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

  /**
    * Signals all clients that the game needs to be started and checks that they all reply.
    * @return Promise that completes with true if all clients replied before the timeout; otherwise false.
    */
  def initGame(level: Level): Promise[Boolean]

  /**
    * Starts the game by notifying the interface and passing the level context to use.
    * @param levelContext The level context.
    */
  def startGame(levelContext: LevelContext): Unit

  /**
    * Signals all clients that the game have been stopped.
    */
  def stopGame(): Unit

  /**
    * Removes the player from the game.
    * @param username The player username.
    */
  def removePlayerFromGame(username: String)

  /**
    * Notifies server about new client input event.
     * @param event The event.
    */
  def notifyClientInputEvent(event: MouseEventWrapper): Unit

  /**
    * Broadcasts all the visible entities to the clients in the lobby.
    * @param entities The list of visible entities to draw.
    */
  def updateClients(entities: Seq[DrawableWrapper]): Unit

  /**
    * Kills this instance.
    */
  def kill(): Unit
}

object Server {
  def apply(username: String = ""): Server = ServerImpl(username)

  final case class ServerImpl(private var username: String) extends Server {

    //the uuid of the cell entity that represents this client
    private var uuid: UUID = _
    //the current lobby
    private var lobby: Option[ServerLobby] = None
    //the actor ref used to send and receive
    private var ref: Option[ActorRef] = None

    //MAIN METHODS

    override def bind(actorRef: ActorRef): Unit = ref = Some(actorRef)

    override def getUUID: UUID = uuid

    override def setUUID(uuid: UUID): Unit = this.uuid = uuid

    override def kill(): Unit = {
      if (ref.nonEmpty) {
        ref.get ! PoisonPill
        ref = None
      }
      if (lobby.nonEmpty) {
        lobby.get.clear()
      }
    }

    //GAME MANAGEMENT

    override def initGame(level: Level): Promise[Boolean] = {
      val promise = Promise[Boolean]()

      //assign player cells to lobby players
      val futures = assignAndSendUUIDToPlayers(level)
      Future.sequence(futures) onComplete {
        case Success(_) =>
          promise success true
        case Failure(_) =>
          promise failure _
      }
      promise
    }

    override def startGame(levelContext: LevelContext): Unit = {
      lobby.get.startGame(levelContext)
    }

    override def stopGame(): Unit = {
      broadcastMessage(ServerActor.GameStopped(false))
      kill()
    }

    override def updateClients(entities: Seq[DrawableWrapper]): Unit = broadcastMessage(entities)

    override def removePlayerFromGame(username: String): Unit = {
      removePlayerFromLobby(username)

    }

    //LOBBY MANAGEMENT

    override def createLobby(lobbyContext: LobbyContext): Unit = {
      lobby = Some(ServerLobby(lobbyContext))
      val address = ActorSystemHolder.systemAddress
      addPlayerToLobby(ref.get, BasicPlayer(username, PlayerInfo(address.host.getOrElse("0.0.0.0"), address.port.getOrElse(0))))
    }

    override def closeLobby(): Unit = {
      broadcastMessage(ServerActor.LobbyClosed)
      lobby = None
    }

    override def getLobbyPlayers: Seq[BasicPlayer] = lobby.get.getPlayers.map(p => p.toBasicPlayer)

    override def removePlayerFromLobby(username: String): Unit = {
      lobby.get.removePlayer(username)
      broadcastMessage(PlayerLeftLobby(username))
    }

    override def addPlayerToLobby(actorRef: ActorRef, player: BasicPlayer): Boolean = {
      if (!lobby.get.isFull) {
        val newPlayer = ReferablePlayer(player.getUsername, player.getInfo, actorRef)
        lobby.get.addPlayer(newPlayer)
        broadcastMessage(PlayerEnteredLobby(newPlayer.toBasicPlayer))
        true
      } else false
    }

    //OBSERVERS MANAGEMENT

    //TODO: event must have uuid
    override def notifyClientInputEvent(event: MouseEventWrapper): Unit = InputEventQueue enqueue event

    //HELPER METHODS

    private def broadcastMessage(message: Any): Unit = {
      lobby.get.getPlayers.foreach(p => p.getRef ! message)
    }

    private def assignAndSendUUIDToPlayers(level: Level): Seq[Future[Any]] = {
      val playerCells = level.entities.filter(_.isInstanceOf[PlayerCellEntity]).map(p => Some(p.getUUID.toString))
      lobby.get.getPlayers.zipAll(playerCells, null, None).map {
        case (p, Some(id)) => p.getRef ? ServerActor.GameStarted(id)
        case (_, None) => throw new IllegalStateException("Not enough player cells for all the clients")
      }
    }
  }
}



