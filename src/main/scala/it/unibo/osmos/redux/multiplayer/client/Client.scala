package it.unibo.osmos.redux.multiplayer.client

import akka.actor.{ActorRef, PoisonPill}
import akka.pattern.ask
import akka.util.Timeout
import it.unibo.osmos.redux.multiplayer.client.ClientActor.{LeaveGame, LeaveLobby, PlayerInput, StartWatching}
import it.unibo.osmos.redux.multiplayer.common.ActorSystemHolder
import it.unibo.osmos.redux.multiplayer.lobby.GameLobby
import it.unibo.osmos.redux.multiplayer.players.BasePlayer
import it.unibo.osmos.redux.multiplayer.server.ServerActor._
import it.unibo.osmos.redux.mvc.controller.levels.structure.{LevelInfo, MapShape}
import it.unibo.osmos.redux.mvc.view.components.multiplayer.User
import it.unibo.osmos.redux.mvc.view.context.{LobbyContext, MultiPlayerLevelContext}
import it.unibo.osmos.redux.mvc.view.drawables.DrawableEntity
import it.unibo.osmos.redux.mvc.view.events.{GameLost, GamePending, GameWon, MouseEventWrapper}
import it.unibo.osmos.redux.utils.{Constants, Logger}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}

trait Client {

  implicit val who: String = "Client"
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
  def getUUID: String

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
    * Starts the game.
    * @param uuid The entity uuid assigned to this client by the server.
    * @param levelInfo The level info.
    * @param mapShape The shape of the level.
    */
  def startGame(uuid: String, levelInfo: LevelInfo, mapShape: MapShape): Unit

  /**
    * Stops the game.
    * @param victorious If this client won or not.
    */
  def stopGame(victorious: Boolean): Unit

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
  def getLobbyPlayers: Seq[BasePlayer]

  /**
    * Removes a player from the lobby.
    * @param username The username of the player.
    */
  def removePlayerFromLobby(username: String): Unit

  /**
    * Adds a player to the lobby.
    * @param player The player to add.
    */
  def addPlayerToLobby(player: BasePlayer): Unit

  /**
    * Closes the lobby.
    * @param byUser If the lobby have been closed by the user or not.
    */
  def closeLobby(byUser: Boolean = true): Unit

  /**
    * Forwards player into to the server.
    * @param event The event.
    */
  def signalPlayerInput(event: MouseEventWrapper): Unit

  /**
    * Notifies the client to redraw.
    * @param entities The entities to draw.
    */
  def notifyRedraw(entities: Seq[DrawableEntity]): Unit
}

object Client {
  def apply(): Client = ClientImpl()

  final case class ClientImpl() extends Client {

    //temp id useful for handshaking
    private var tempID: String = _
    //the username of this specific client
    private var username: String = _
    //the uuid of the cell entity that represents this client
    private var uuid: String = _

    //the current lobby
    private var lobby: Option[GameLobby[BasePlayer]] = None
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
      Logger.log(s"connect --> $address:$port")

      val promise = Promise[Boolean]()
      resolveRemotePath(generateRemoteActorPath(address, port)) onComplete {
        case Success(serverRef) =>
          server = Some(serverRef)
          Logger.log(s"Server found --> ${server.get.path}")
          serverRef ? ClientActor.Connect(ref.get) onComplete {
            case Success(Connected(id)) =>
              Logger.log(s"Received tempID --> $id")
              this.tempID = id; promise success true
            case Success(_) => promise success false
            case Failure(t) => kill(); promise failure t
          }
        case Failure(t) => kill(); promise failure t
      }
      promise
    }

    override def getUUID: String = uuid

    override def kill(): Unit = {
      Logger.log("kill")

      if (lobby.nonEmpty && server.nonEmpty) {
        if (uuid.nonEmpty && levelContext.nonEmpty) server.get ! LeaveGame(username)
        else server.get ! LeaveLobby(username)
        lobby = None
      }
      if (levelContext.nonEmpty) {
        levelContext = None
      }
      if (server.nonEmpty) {
        server = None
      }
      if (ref.nonEmpty) {
        ref.get ! PoisonPill
        ref = None
      }
    }

    //GAME MANAGEMENT

    override def initGame(levelContext: MultiPlayerLevelContext): Unit = {
      Logger.log("initGame")

      if (lobby.isEmpty) throw new IllegalStateException("The player entered no lobby, unable to initialize the game.")
      if (this.levelContext.nonEmpty) throw new IllegalStateException("The server already hold a level context, unable to initialize the game.")

      //register client to the mouse event listener to send input events to the server
      levelContext.subscribe(e => { signalPlayerInput(e) })

      this.levelContext = Some(levelContext)
    }

    override def startGame(uuid: String, levelInfo: LevelInfo, mapShape: MapShape): Unit = {
      //save entity uuid
      this.uuid = uuid
      //update level context uuid
      levelContext.get.setPlayerUUID(uuid)

      if (uuid.equals(Constants.MultiPlayer.defaultClientUUID)) throw new IllegalArgumentException("Invalid player UUID, the client is not be able to send correct inputs to the server")

      //notify lobby that the game is started (prepares the view)
      lobby.get.notifyGameStarted(levelContext.get, levelInfo)
      //actually starts the game
      levelContext.get.setupLevel(mapShape)
    }

    override def stopGame(victorious: Boolean): Unit = {
      Logger.log(s"notifyGameStatusChanged - victorious: $victorious")

      //game is won or lost
      levelContext.get.notify(if (victorious) GameWon else GameLost)
    }

    override def leaveGame(): Unit = {
      Logger.log("leaveGame")

      if (username.isEmpty) throw new IllegalStateException("The player entered no lobby, unable to leave.")

      //send leave game only if it's the user will (a.k.a. the game state is still in pending)
      if (levelContext.nonEmpty && levelContext.get.gameCurrentState == GamePending) server.get.tell(ClientActor.LeaveGame(username), ref.get)
    }

    //INPUT MANAGEMENT

    override def signalPlayerInput(event: MouseEventWrapper): Unit = {
      Logger.log("signalPlayerInput")

      server.get ! PlayerInput(event)
    }

    //LOBBY

    override def enterLobby(username: String, lobbyContext: LobbyContext): Promise[Boolean] = {
      Logger.log("enterLobby")

      if (lobby.nonEmpty) throw new IllegalStateException("Unable to enter lobby if the client is already entered in another one")
      if (tempID.isEmpty) throw new IllegalStateException("Unable to enter lobby if the client hasn't yet received the temp id from the server")

      //Save username locally
      this.username = username

      val promise = Promise[Boolean]()
      server.get ? ClientActor.EnterLobby(tempID, username) onComplete {
        case Success(LobbyInfo(players)) =>
          lobby = Some(GameLobby(lobbyContext))
          lobby.get.addPlayers(players: _*)
          //because the interface is not ready yet, set users list into lobby context
          lobbyContext.users = players.map(p => new User(p, false))
          //add watch to server remote actor
          ref.get ! StartWatching(server.get)
          //fulfill promise
          promise success true
        case Success(Disconnected) | Success(UsernameAlreadyTaken) | Success(LobbyFull) => kill(); promise success false
        case Success(unknown) =>
          kill(); promise failure new IllegalArgumentException(s"Server unexpected replied to enterLobby with unknown message: $unknown")
        case Failure(t) => kill(); promise failure t
      }
      promise
    }

    override def leaveLobby(): Unit = {
      Logger.log("leaveLobby")

      if (username.isEmpty) throw new IllegalStateException("The player entered no lobby, unable to leave.")

      server.get.tell(LeaveLobby(username), ref.get)
      closeLobby()
    }

    override def closeLobby(byUser: Boolean = true): Unit = {
      Logger.log("closeLobby")

      if (lobby.isEmpty) throw new IllegalStateException("The player entered no lobby, unable to leave.")

      lobby.get.notifyLobbyClosed(byUser)
      lobby = None
    }

    override def getLobbyPlayers: Seq[BasePlayer] = {
      Logger.log("getLobbyPlayers")

      lobby.get.getPlayers
    }

    override def addPlayerToLobby(player: BasePlayer): Unit = {
      Logger.log("addPlayerToLobby")

      lobby.get.addPlayer(player)
    }

    override def removePlayerFromLobby(username: String): Unit = {
      Logger.log("removePlayerFromLobby")

      lobby.get.removePlayer(username)
    }

    //OBSERVERS MANAGEMENT

    override def notifyRedraw(entities: Seq[DrawableEntity]): Unit = {
      Logger.log("notifyRedraw")

      val player = entities.find(_.getUUID == getUUID)
      if (player.isEmpty) throw new IllegalArgumentException("Unable to draw entities because the player was not found")
      levelContext.get.drawEntities(player, entities)
    }

    //HELPERS

    private def generateRemoteActorPath(address: String, port: Int): String = {
      s"""akka.tcp://${Constants.MultiPlayer.defaultSystemName}@$address:$port/user/${Constants.MultiPlayer.defaultServerActorName}"""
    }

    private def resolveRemotePath(remotePath: String): Future[ActorRef] = {
      Logger.log("resolveRemotePath")

      val selection = ActorSystemHolder.getSystem.actorSelection(remotePath)
      selection resolveOne()
    }
  }
}
