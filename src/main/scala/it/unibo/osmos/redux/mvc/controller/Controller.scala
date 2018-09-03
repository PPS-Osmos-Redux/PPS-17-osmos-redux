package it.unibo.osmos.redux.mvc.controller

import it.unibo.osmos.redux.ecs.engine.GameEngine
import it.unibo.osmos.redux.ecs.entities.{CellEntity, PlayerCellEntity}
import it.unibo.osmos.redux.multiplayer.client.Client
import it.unibo.osmos.redux.multiplayer.common.{ActorSystemHolder, MultiPlayerMode}
import it.unibo.osmos.redux.multiplayer.server.Server
import it.unibo.osmos.redux.mvc.model.{VictoryRules, _}
import it.unibo.osmos.redux.mvc.view.components.multiplayer.User
import it.unibo.osmos.redux.mvc.view.context._
import it.unibo.osmos.redux.mvc.view.events.{AbortLobby, _}
import it.unibo.osmos.redux.utils.{Constants, GenericResponse, Logger}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Promise
import scala.util.{Failure, Success}

/**
  * Controller base trait
  */
trait Controller {
  type MultiPlayerMode = MultiPlayerMode.Value
  type LevelContextType = LevelContextType.Value

  /**
    * Initializes the level and the game engine.
    * @param levelContext The level context.
    * @param chosenLevel The name of the chosen level.
    * @param isCustom True if the level is a custom one, false otherwise
    * @return None if level loaded with success, Some(String) if exception occurs
    */
  def initLevel(levelContext: LevelContext, chosenLevel: String, isCustom: Boolean = false): GenericResponse[Boolean]

  /**
    * Initializes the multi-player lobby and the server or client.
    * @param user The user config
    * @param lobbyContext The lobby context
    * @return Promise that completes with true if the lobby is initialized successfully; otherwise false.
    */
  def initLobby(user: User, lobbyContext: LobbyContext): Promise[GenericResponse[Boolean]]

  /**
    * Initializes the multi-player level and the game engine.
    * @param levelInfo The level info
    * @return Promise that completes with true if the level is initialized successfully; otherwise false.
    */
  def initMultiPlayerLevel(levelInfo: LevelInfo): Promise[GenericResponse[Boolean]]

  /**
    * Starts the level.
    */
  def startLevel(): Unit

  /**
    * Stops the level.
    */
  def stopLevel(): Unit

  /**
    * Pauses the level.
    */
  def pauseLevel(): Unit

  /**
    * Resumes the level.
    */
  def resumeLevel(): Unit

  /**
    * Saves a level.
    * @param name level name
    * @param map level map shape MapShape
    * @param victoryRules level victory rule VictoryRule.Value
    * @param collisionRules level collision rule CollisionRule.Value
    * @param entities Seq of CellEntity
    * @return True, if the operation is successful; otherwise false.
    **/
  def saveLevel(name: String, map:MapShape, victoryRules: VictoryRules.Value, collisionRules: CollisionRules.Value, entities: Seq[CellEntity]): Boolean

  /**
    * Delete from file a custom level
    * @param name custom level name
    * @return true, if remove file is completed with success
    */
  def removeLevel(name:String): Boolean

  /**
    * Gets all the levels in the campaign.
    * @return The list of LevelInfo.
    */
  def getSinglePlayerLevels:List[LevelInfo] = SinglePlayerLevels.getLevels

  /**
    * Gets all multi-player levels.
    * @return The list of multi-player levels.
    */
  def getMultiPlayerLevels: List[LevelInfo] = MultiPlayerLevels.getLevels

  /**
    * Gets all custom levels filename.
    * @return The list of custom levels filename.
    */
  def getCustomLevels: List[LevelInfo]

  /**
    * Get requested sound path
    * @param soundType SoundsType.Value
    * @return Some(String) if path exists
    */
  def getSoundPath(soundType: SoundsType.Value): Option[String]

}

case class ControllerImpl() extends Controller with GameStateHolder {

  implicit val who: String = "Controller"

  var lastLoadedLevel:Option[String] = None
  var engine:Option[GameEngine] = None

  //multi-player variables
  private var multiPlayerMode: Option[MultiPlayerMode] = None
  private var server: Option[Server] = None
  private var client: Option[Client] = None

  override def initLevel(levelContext: LevelContext, chosenLevel: String, isCustom: Boolean = false): GenericResponse[Boolean] = {
    Logger.log("initLevel")

    var loadedLevel:Option[Level] = None

    if (isCustom) {
      loadedLevel = FileManager.loadCustomLevel(chosenLevel)
      /*Because user stats are not influenced by custom level end game results*/
      lastLoadedLevel = None
    } else {
      loadedLevel = FileManager.loadResource(chosenLevel)
      lastLoadedLevel = Some(chosenLevel)
    }

    if (loadedLevel.isDefined) {
      loadedLevel.get.isSimulation = levelContext.levelContextType == LevelContextType.simulation

      val player = loadedLevel.get.entities.find(_.isInstanceOf[PlayerCellEntity])
      if (player.isEmpty && !loadedLevel.get.isSimulation) return GenericResponse(false, "Cannot start a normal level if the player is not present")
      //assign current player uuid to the
      if(player.isDefined) levelContext.setPlayerUUID(player.get.getUUID)

      //create and initialize the game engine
      if(engine.isEmpty) engine = Some(GameEngine())
      //TODO: engine must need a GameStateHolder for the EndGameSystem
      engine.get.init(loadedLevel.get, levelContext)
      levelContext.setupLevel(loadedLevel.get.levelMap.mapShape)
      GenericResponse(true)
    } else {
      GenericResponse(false, "Error: level " + chosenLevel + " not found! The level " + (if(isCustom) "is" else "isn't") + "custom level")
    }
  }

  override def initLobby(user: User, lobbyContext: LobbyContext): Promise[GenericResponse[Boolean]] = {
    Logger.log("initLobby")

    val promise = Promise[GenericResponse[Boolean]]()

    //subscribe to lobby context to intercept exit from lobby click
    lobbyContext.subscribe {
      //if user is defined that the event is from the user and not from the server
      case LobbyEventWrapper(AbortLobby, Some(_)) =>
        multiPlayerMode match {
          case Some(MultiPlayerMode.Server) =>
            if (server.nonEmpty) {
              server.get.closeLobby()
              server.get.kill()
              multiPlayerMode = None
              server = None
            }
            client = None
          case Some(MultiPlayerMode.Client) =>
            if (client.nonEmpty) {
              client.get.leaveLobby()
              client.get.kill()
              multiPlayerMode = None
              client = None
            }
          case _ => //do nothing
        }
      case _ => //do nothing
    }

    multiPlayerMode = Some(if (user.isServer) MultiPlayerMode.Server else MultiPlayerMode.Client)
    multiPlayerMode match {
      case Some(MultiPlayerMode.Server) =>
        //initialize the server and creates the lobby
        val server = Server(user.username)
        server.bind(ActorSystemHolder.createActor(server))
        //create lobby
        server.createLobby(lobbyContext)
        //save server reference
        this.server = Some(server)
        promise success GenericResponse(true)

      case Some(MultiPlayerMode.Client) =>
        //initialize the client, connects to the server and enters the lobby
        val client = Client()
        client.bind(ActorSystemHolder.createActor(client))
        client.connect(user.ip, user.port).future onComplete {
          case Success(true) => client.enterLobby(user.username, lobbyContext).future onComplete {
            case Success(true) =>
              this.client = Some(client)
              //creates the level context
              //TODO: think about a better way, technically getUUID method of the client won't be called until the game is started (the GameStarted message will carry along this value).
              val levelContext = LevelContext(Constants.MultiPlayer.defaultClientUUID)
              //initializes the game
              client.initGame(levelContext)
              //fulfill promise
              promise success GenericResponse(true)
            case Success(false) => promise success GenericResponse(false, "Unable to enter the lobby, unknown error occurred")
            case Failure(t) => promise success GenericResponse(false, s"Unable to enter the lobby: ${t.getMessage}")
          }
          case Success(false) => promise success GenericResponse(false, "Unable to connect to the server, unknown error")
          case Failure(t) => promise success GenericResponse(false, s"Unable to connect to the server: ${t.getMessage}")
        }
      case _ =>
        promise success GenericResponse(false, "Cannot initialize the lobby if the multi-player mode is not defined")
    }
    promise
  }

  override def initMultiPlayerLevel(levelInfo: LevelInfo): Promise[GenericResponse[Boolean]] = {
    Logger.log("initMultiPlayerLevel")

    val promise = Promise[GenericResponse[Boolean]]()

    //load level definition
    val loadedLevel = FileManager.loadResource(levelInfo.name, isMultiPlayer = true).get

    multiPlayerMode.get match {
      case MultiPlayerMode.Server =>
        //assign clients to players and wait confirmation
        server.get.initGame(loadedLevel, levelInfo).future onComplete {
          case Success(_) =>
            //create the engine
            if (engine.isEmpty) engine = Some(GameEngine())
            //initialize the engine and let him create the levelContext
            val levelContext = engine.get.init(loadedLevel, server.get)

            //signal server that the game is ready to be started
            server.get.startGame(levelContext, levelInfo)
            //tell view to actually start the game
            levelContext.setupLevel(loadedLevel.levelMap.mapShape)

            //fulfill the promise
            promise success GenericResponse(true)
          case Failure(t) => promise success GenericResponse(false, s"Unable to initialize the game: ${t.getMessage}")
        }
      case _ =>
        promise success GenericResponse(false, "Unable to initialize multi-player level if no lobby have been created.")
    }
    promise
  }

  override def startLevel(): Unit = {
    Logger.log("startLevel")

    multiPlayerMode match {
      case Some(MultiPlayerMode.Server) | None => if (engine.isDefined) engine.get.start()
      case _ =>
    }
  }

  override def stopLevel(): Unit = {
    Logger.log("stopLevel")

    multiPlayerMode match {
      case Some(MultiPlayerMode.Client) =>
        if (client.isDefined) client.get.leaveGame()
      case _ =>
        if (server.isDefined) server.get.stopGame()
        if (engine.isDefined) engine.get.stop()
    }
  }

  override def pauseLevel(): Unit = {
    Logger.log("pauseLevel")

    multiPlayerMode match {
      case None => if (engine.isDefined) engine.get.pause()
      case _ => throw new UnsupportedOperationException("A multi-player level cannot be paused.")
    }
  }

  override def resumeLevel(): Unit = {
    Logger.log("resumeLevel")

    multiPlayerMode match {
      case None => if (engine.isDefined) engine.get.resume()
      case _ => throw new UnsupportedOperationException("A multi-player level cannot be resumed.")
    }
  }

  //TODO useless for controller
  override def gameCurrentState: GameStateEventWrapper = ???

  override def getSoundPath(soundType: SoundsType.Value): Option[String] = soundType match {
    case SoundsType.menu => Some(FileManager.loadMenuMusic())
    case SoundsType.level => Some(FileManager.loadLevelMusic())
    case SoundsType.button => Some(FileManager.loadButtonsSound())
    case _ => Logger.log("Sound type not managed!! [getSoundPath]")
              None
  }

  override def notify(event: GameStateEventWrapper): Unit = lastLoadedLevel match {
    case Some(lastLevel:String) => SinglePlayerLevels.newEndGameEvent(event, lastLevel)
      FileManager.saveUserProgress(SinglePlayerLevels.userStatistics)
    case _ =>
  }

  override def getCustomLevels: List[LevelInfo] = FileManager.customLevelsFilesName match {
    case Success(customLevels) => customLevels
    case Failure(_) => Logger.log("[Info] User doesn't have any saved custom level or custom level directory doesn't exists")
                               List()
  }

  override def saveLevel(name: String, map: MapShape, victoryRules: VictoryRules.Value, collisionRules: CollisionRules.Value, entities: Seq[CellEntity]): Boolean = {
    val lv: Level = Level(LevelInfo(name, victoryRules) , LevelMap(map, collisionRules), entities.toList)
    lv.checkCellPosition()
    FileManager.saveLevel(lv).isDefined
  }

  override def removeLevel(name: String): Boolean = FileManager.deleteLevel(name) match {
    case Success(_) => true
    case Failure(exception) => Logger.log("Error occurred removing custom level file" + exception.getMessage)
                               false
  }
}
