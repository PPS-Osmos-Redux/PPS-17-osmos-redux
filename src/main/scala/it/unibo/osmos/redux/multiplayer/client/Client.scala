package it.unibo.osmos.redux.multiplayer.client

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import it.unibo.osmos.redux.multiplayer.common.ActorSystemHolder
import it.unibo.osmos.redux.multiplayer.lobby.ClientLobby
import it.unibo.osmos.redux.multiplayer.players.BasicPlayer
import it.unibo.osmos.redux.multiplayer.server.ServerActor
import it.unibo.osmos.redux.mvc.view.components.multiplayer.User
import it.unibo.osmos.redux.mvc.view.context.{LobbyContext, MultiPlayerLevelContext}
import it.unibo.osmos.redux.mvc.view.drawables.DrawableEntity
import it.unibo.osmos.redux.mvc.view.events.{GamePending, GameStateEventWrapper, MouseEventWrapper}
import it.unibo.osmos.redux.utils.{Constants, Logger}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}

trait Client {

  implicit val timeout: Timeout = Timeout(5.minutes) //TODO: change to 5 sec

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
  def getUUID: String

  /**
    * Sets the uuid of the cell entity that represents this client.
    * @param uuid The uuid
    */
  def setUUID(uuid: String): Unit

  /**
    * Kills this instance.
    */
  def kill(): Unit

  /**
    * Initializes the game.
    * @param levelContext The level context.
    */
  def initGame(levelContext: MultiPlayerLevelContext): Unit

  /**
    * Leaves the game.
    */
  def leaveGame(): Unit

  /**
    * Requests to enter the lobby.
    * @param username The username of the player.
    * @param lobbyContext The lobby context.
    * @return Promise that completes with true if the client have successfully entered the lobby; otherwise false.
    */
  def enterLobby(username: String, lobbyContext: LobbyContext): Promise[Boolean]

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
    * Notifies the client that the game status have changed.
    * @param status The current status of the game.
    */
  def notifyGameStatusChanged(status: GameStateEventWrapper): Unit

  /**
    * Notifies the client to redraw.
    * @param entities The entities to draw.
    */
  def notifyRedraw(entities: Seq[DrawableEntity]): Unit
}

object Client {
  def apply(): Client = ClientImpl()

  final case class ClientImpl() extends Client {

    implicit val who: String = "Client"

    //the username of this specific client
    private var username: String = _
    //the uuid of the cell entity that represents this client
    private var uuid: String = _

    //the current lobby
    private var lobby: Option[ClientLobby] = None
    //the server actor
    private var server: Option[ActorRef] = None
    //the client actor
    private var ref: Option[ActorRef] = None

    //the level context of the current level
    private var levelContext: Option[MultiPlayerLevelContext] = None

    //MAIN METHODS

    override def bind(actorRef: ActorRef): Unit = ref = {
      Logger.log("bind")

      Some(actorRef)
    }

    override def connect(address: String, port: Int): Promise[Boolean] = {
      Logger.log("connect")

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

    override def getUUID: String = uuid

    override def setUUID(uuid: String): Unit = this.uuid = uuid

    override def kill(): Unit = {
      Logger.log("kill")

      if (levelContext.nonEmpty) {
        levelContext = None
      }
      if (ref.nonEmpty) {
        ActorSystemHolder.stopActor(ref.get)
        ref = None
      }
      if (server.nonEmpty) {
        server = None
      }
      clearLobby()
    }

    //GAME MANAGEMENT

    override def initGame(levelContext: MultiPlayerLevelContext): Unit = {
      Logger.log("initGame")

      if (lobby.isEmpty) throw new IllegalStateException("The player entered no lobby, unable to initialize the game.")
      if (this.levelContext.nonEmpty) throw new IllegalStateException("The server already hold a level context, unable to initialize the game.")

      //register client to the mouse event listener to send input events to the server
      levelContext.subscribe(e => { signalPlayerInput(e) })

      this.levelContext = Some(levelContext)

      //TODO: probably from now lobby is no more useful
    }

    override def leaveGame(): Unit = {
      Logger.log("leaveGame")

      if (username.isEmpty) throw new IllegalStateException("The player entered no lobby, unable to leave.")
      server.get ! ClientActor.LeaveGame(username)
      kill()
    }

    //INPUT MANAGEMENT

    override def signalPlayerInput(event: MouseEventWrapper): Unit = {
      Logger.log("signalPlayerInput")

      server.get ! ClientActor.PlayerInput(event)
    }

    //LOBBY

    override def enterLobby(username: String, lobbyContext: LobbyContext): Promise[Boolean] = {
      Logger.log("enterLobby")

      if (lobby.nonEmpty) throw new IllegalStateException("Unable to enter lobby if the client is already entered in another one")
      //Save username locally
      this.username = username

      val promise = Promise[Boolean]()
      server.get ? ClientActor.EnterLobby(username) onComplete {
        case Success(result) => result match {
          case ServerActor.LobbyInfo(players) =>
            lobby = Some(ClientLobby(lobbyContext))
            lobby.get.addPlayers(players: _*)
            //because the interface is not ready yet, set users list into lobby context
            lobbyContext.users = players.map(p => new User(p, false))
            //fulfill promise
            promise success true
          case ServerActor.UsernameAlreadyTaken | ServerActor.LobbyFull =>
            //fulfill promise reporting an error
            promise success false
        }
        case Failure(t) => promise failure t
      }
      promise
    }

    override def leaveLobby(): Unit = {
      Logger.log("leaveLobby")

      if (username.isEmpty) throw new IllegalStateException("The player entered no lobby, unable to leave.")

      server.get.tell(ClientActor.LeaveLobby(username), ref.get)
      clearLobby()
    }

    override def getLobbyPlayers: Seq[BasicPlayer] = {
      Logger.log("getLobbyPlayers")

      lobby.get.getPlayers
    }

    override def addPlayerToLobby(player: BasicPlayer): Unit = {
      Logger.log("addPlayerToLobby")

      lobby.get.addPlayer(player)
    }

    override def removePlayerFromLobby(username: String): Unit = {
      Logger.log("removePlayerFromLobby")

      lobby.get.removePlayer(username)
    }

    override def clearLobby(): Unit = {
      Logger.log("clearLobby")

      if (lobby.nonEmpty) {
        lobby.get.clear()
        lobby = None
      }
    }

    //OBSERVERS MANAGEMENT

    override def notifyGameStatusChanged(status: GameStateEventWrapper): Unit = {
      Logger.log("notifyGameStatusChanged")

      status match {
        case GamePending =>
          //notify lobby that the game is started
          lobby.get.startGame(levelContext.get)
        case gameState =>
          //game is won or lost
          levelContext.get.notify(gameState)
      }
    }

    override def notifyRedraw(entities: Seq[DrawableEntity]): Unit = {
      Logger.log("notifyRedraw")

      val player = entities.find(_.getUUID == getUUID)
      if (player.isEmpty) throw new IllegalArgumentException("Unable to draw entities because the player was not found")
      levelContext.get.drawEntities(player, entities)
    }

    //HELPERS

    private def generateRemoteActorPath(address: String, port: Int): String = {
      s"""akka.tcp://${Constants.defaultSystemName}@$address:$port/user/${Constants.defaultServerActorName}"""
    }

    private def resolveRemotePath(remotePath: String): Future[ActorRef] = {
      Logger.log("resolveRemotePath")

      val selection = ActorSystemHolder.getSystem.actorSelection(remotePath)
      selection resolveOne()
    }
  }
}
