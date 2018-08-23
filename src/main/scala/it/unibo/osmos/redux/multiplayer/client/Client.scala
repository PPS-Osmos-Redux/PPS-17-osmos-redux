package it.unibo.osmos.redux.multiplayer.client

import java.util.UUID

import akka.actor.{ActorRef, PoisonPill}
import akka.pattern.ask
import akka.util.Timeout
import it.unibo.osmos.redux.multiplayer.common.ActorSystemHolder
import it.unibo.osmos.redux.multiplayer.lobby.ClientLobby
import it.unibo.osmos.redux.multiplayer.players.BasicPlayer
import it.unibo.osmos.redux.multiplayer.server.ServerActor
import it.unibo.osmos.redux.mvc.view.drawables.DrawableWrapper
import it.unibo.osmos.redux.mvc.view.events.{GameStateEventWrapper, MouseEventWrapper}
import it.unibo.osmos.redux.utils.Constants

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}

trait Client {

  implicit val timeout: Timeout = Timeout(5.seconds)

  /**
    * Binds this instance with the input ActorRef.
    * @param actorRef The ActorRef
    */
  def bind(actorRef: ActorRef): Unit

  /**
    * Connects to a server.
    * @param address The server address.
    * @param port The port address.
    * @return Promise that completes with true if the connection has been successfully established; otherwise false.
    */
  def connect(address: String, port: Int): Promise[Boolean]

  /**
    * Gets the uuid of the cell entity that represents this client.
    * @return The uuid
    */
  def getUUID: UUID

  /**
    * Sets the uuid of the cell entity that represents this client.
    * @param uuid The uuid
    */
  def setUUID(uuid: UUID): Unit

  /**
    * Kills this instance.
    */
  def kill(): Unit

  /**
    * Leaves the game.
    */
  def leaveGame(): Unit

  /**
    * Requests to enter the lobby.
    * @param username The username of the player.
    * @return Promise that completes with true if the client have successfully entered the lobby; otherwise false.
    */
  def enterLobby(username: String): Promise[Boolean]

  /**
    * Requests to leave the lobby.
    */
  def leaveLobby(): Unit

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
    * @param player The player to add.
    */
  def addPlayerToLobby(player: BasicPlayer): Unit

  /**
    * Clears the lobby.
    */
  def clearLobby(): Unit

  /**
    * Forwards player into to the server.
    * @param event The event.
    */
  def signalPlayerInput(event: MouseEventWrapper): Unit

  /**
    * Subscribes an observer to the game status changed events.
    * @param observer The observer.
    */
  def subscribeGameStatusChangedEvent(observer: GameStatusChangedObserver): Unit

  /**
    * Notifies all observer of a change in the game status.
    * @param status The current status of the game.
    */
  def notifyGameStatusChanged(status: GameStateEventWrapper): Unit

  /**
    * Subscribes an observer to draw entity events.
    * @param observer The observer.
    */
  def subscribeEntityDrawEvent(observer: DrawEntityObserver): Unit

  /**
    * Notifies all observer new entities to draw.
    * @param entities The entities.
    */
  def notifyEntityToDraw(entities: Seq[DrawableWrapper]): Unit
}

object Client {
  def apply(): Client = ClientImpl()

  final case class ClientImpl() extends Client {

    //the username of this specific client
    private var username: String = _
    //the uuid of the cell entity that represents this client
    private var uuid: UUID = _

    //the current lobby
    private var lobby: Option[ClientLobby] = None

    //the server actor
    private var server: Option[ActorRef] = None
    //the client actor
    private var ref: Option[ActorRef] = None

    //the observers of the game status
    private var gameStatusObservers: mutable.Set[GameStatusChangedObserver] = mutable.Set()
    //the observer for draw entity event
    private var drawEntityObservers: mutable.Set[DrawEntityObserver] = mutable.Set()

    //MAIN METHODS

    override def bind(actorRef: ActorRef): Unit = ref = Some(actorRef)

    override def connect(address: String, port: Int): Promise[Boolean] = {
      val promise = Promise[Boolean]()
      resolveRemotePath(generateRemoteActorPath(address, port)) onComplete {
        case Success(serverRef) =>
          server = Some(serverRef)
          serverRef ? ClientActor.Connect onComplete {
            case Success(response) => response match {
              case ServerActor.Established => promise success true
              case _ => promise success false
            }
            case Failure(_) => promise failure _
          }
        case Failure(_) => promise failure _
      }
      promise
    }

    override def getUUID: UUID = uuid

    override def setUUID(uuid: UUID): Unit = this.uuid = uuid

    override def kill(): Unit = {
      gameStatusObservers.clear()
      if (ref.nonEmpty) {
        ref.get ! PoisonPill
        ref = None
      }
      if (server.nonEmpty) {
        server = None
      }
      clearLobby()
    }

    //GAME MANAGEMENT

    override def leaveGame(): Unit = {
      if (username.isEmpty) throw new IllegalStateException("The player entered no lobby, unable to leave.")
      server.get ! ClientActor.LeaveGame(username)
      kill()
    }

    //INPUT MANAGEMENT

    //TODO: add UUID to Mouse
    override def signalPlayerInput(event: MouseEventWrapper): Unit = server.get ! ClientActor.PlayerInput(uuid.toString, event)

    //LOBBY

    override def enterLobby(username: String): Promise[Boolean] = {
      if (lobby.nonEmpty) throw new IllegalStateException("Unable to enter lobby if the client is already entered in another one")
      //Save username locally
      this.username = username

      val promise = Promise[Boolean]()
      server.get ? ClientActor.EnterLobby(username) onComplete { //TODO: forse necessario passare actorRef
        case Success(result) => result match {
          case ServerActor.LobbyInfo(players) =>
            lobby.get.addPlayers(players: _*)
            promise success true
          case ServerActor.UsernameAlreadyTaken =>
            promise success false
        }
        case Failure(_) => promise failure _
      }
      promise
    }

    override def leaveLobby(): Unit = {
      if (username.isEmpty) throw new IllegalStateException("The player entered no lobby, unable to leave.")

      server.get.tell(ClientActor.LeaveLobby(username), ref.get)
      lobby = None
    }

    override def getLobbyPlayers: Seq[BasicPlayer] = lobby.get.getPlayers

    override def addPlayerToLobby(player: BasicPlayer): Unit = lobby.get.addPlayer(player)

    override def removePlayerFromLobby(username: String): Unit = lobby.get.removePlayer(username)

    override def clearLobby(): Unit = {
      if (lobby.nonEmpty) {
        lobby.get.clear()
        lobby = None
      }
    }

    //OBSERVERS MANAGEMENT

    override def notifyGameStatusChanged(status: GameStateEventWrapper): Unit = gameStatusObservers foreach(o => o.update(status))

    override def subscribeGameStatusChangedEvent(observer: GameStatusChangedObserver): Unit = gameStatusObservers += observer

    override def subscribeEntityDrawEvent(observer: DrawEntityObserver): Unit = drawEntityObservers += observer

    override def notifyEntityToDraw(entities: Seq[DrawableWrapper]): Unit = drawEntityObservers foreach(o => o.update(entities))

    //HELPERS

    private def generateRemoteActorPath(address: String, port: Int): String = {
      s"""akka.tcp://${Constants.defaultSystemName}@$address:$port/user/${Constants.defaultServerActorName}"""
    }

    private def resolveRemotePath(remotePath: String): Future[ActorRef] = {
      val selection = ActorSystemHolder.getSystem.actorSelection(remotePath)
      selection resolveOne()
    }
  }
}
