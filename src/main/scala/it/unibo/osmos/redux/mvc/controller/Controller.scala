package it.unibo.osmos.redux.mvc.controller

import it.unibo.osmos.redux.ecs.engine.{GameEngine, GameStatus}
import it.unibo.osmos.redux.ecs.entities.{CellEntity, PlayerCellEntity}
import it.unibo.osmos.redux.multiplayer.client.Client
import it.unibo.osmos.redux.multiplayer.common.{ActorSystemHolder, MultiPlayerMode}
import it.unibo.osmos.redux.multiplayer.server.{Server, ServerState}
import it.unibo.osmos.redux.mvc.controller.levels.{MultiPlayerLevels, SinglePlayerLevels}
import it.unibo.osmos.redux.mvc.controller.levels.structure._
import it.unibo.osmos.redux.mvc.controller.manager.files.{LevelFileManager, SoundFileManager, UserProgressFileManager}
import it.unibo.osmos.redux.mvc.controller.manager.sounds.SoundsType
import it.unibo.osmos.redux.mvc.view.components.multiplayer.User
import it.unibo.osmos.redux.mvc.view.context._
import it.unibo.osmos.redux.mvc.view.events.{AbortLobby, _}
import it.unibo.osmos.redux.utils.{Constants, GenericResponse, Logger}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Promise
import scala.util.{Failure, Success, Try}

/**Controller base trait*/
trait Controller {
  type MultiPlayerMode = MultiPlayerMode.Value
  type LevelContextType = LevelContextType.Value

  /** Initializes the level and the game engine.
    *
    * @param levelContext The level context.
    * @param chosenLevel The name of the chosen level.
    * @param isCustom True if the level is a custom one, false otherwise
    */
  def initLevel(levelContext: LevelContext, chosenLevel: String, isCustom: Boolean = false): Try[Unit]

  /** Initializes the multi-player lobby and the server or client.
    *
    * @param user The user config
    * @param lobbyContext The lobby context
    * @return Promise that completes with true if the lobby is initialized successfully; otherwise false.
    */
  def initLobby(user: User, lobbyContext: LobbyContext): Promise[GenericResponse[Boolean]]

  /** Initializes the multi-player level and the game engine.
    *
    * @param levelInfo The level info
    * @return Promise that completes with true if the level is initialized successfully; otherwise false.
    */
  def initMultiPlayerLevel(levelInfo: LevelInfo): Promise[GenericResponse[Boolean]]

  /**Starts the level.*/
  def startLevel(): Unit

  /** Stops the level.
    *
    * @param victory the level have been won or lost.
    */
  def stopLevel(victory: Boolean = false): Unit

  /**Pauses the level.*/
  def pauseLevel(): Unit

  /**Resumes the level.*/
  def resumeLevel(): Unit

  /** Changes the level speed by speeding up or slowing down depending on the input.
    *
    * @param increment Determines whether the level speed needs to be increased or decreased.
    */
  def changeLevelSpeed(increment: Boolean = false): Unit

  /** Saves a level.
    *
    * @param name level name
    * @param map level map shape MapShape
    * @param victoryRules level victory rule VictoryRule.Value
    * @param collisionRules level collision rule CollisionRule.Value
    * @param entities Seq of CellEntity
    * @return True, if the operation is successful; otherwise false.
    **/
  def saveLevel(name: String, map:MapShape, victoryRules: VictoryRules.Value, collisionRules: CollisionRules.Value, entities: Seq[CellEntity]): Boolean

  /** Delete from file a custom level
    *
    * @param name custom level name
    * @return true, if remove file is completed with success
    */
  def removeLevel(name:String): Boolean

  /** Gets all the levels in the campaign.
    *
    * @return The list of LevelInfo.
    */
  def getSinglePlayerLevels:List[LevelInfo] = SinglePlayerLevels.getLevelsInfo

  /** Gets all multi-player levels.
    *
    * @return The list of multi-player levels.
    */
  def getMultiPlayerLevels: List[LevelInfo] = MultiPlayerLevels.getLevels

  /** Gets all custom levels filename.
    *
    * @return The list of custom levels filename.
    */
  def getCustomLevels: List[LevelInfo]

  /** Get requested sound path
    *
    * @param soundType SoundsType.Value
    * @return Some(String) if path exists
    */
  def getSoundPath(soundType: SoundsType.Value): Option[String]

  /** Get campaign levels.
    *
    * @return List[CampaignLevel].
    */
  def getCampaignLevels:List[CampaignLevel] = SinglePlayerLevels.getCampaignLevels
}

case class ControllerImpl() extends Controller {

  implicit val who: String = "Controller"

  var lastLoadedLevel:Option[String] = None
  var engine:Option[GameEngine] = None

  //multi-player variables
  private var multiPlayerMode: Option[MultiPlayerMode] = None
  private var server: Option[Server] = None
  private var client: Option[Client] = None

  override def initLevel(levelContext: LevelContext, chosenLevel: String, isCustom: Boolean = false): Try[Unit] = {
    Logger.log("initLevel")

    val isSimulation = levelContext.levelContextType == LevelContextType.simulation
    val loadedLevel:Option[Level] = loadLevel(chosenLevel, isCustom, isSimulation)

    Try(loadedLevel match {
      case Some(level) =>
        //first available player cell is assigned to the user
        level.entities.find(_.isInstanceOf[PlayerCellEntity]) match {
          case Some(player) =>
            //assign current player uuid to the
            levelContext.setPlayerUUID(player.getUUID)
            //clear all extra player cells
            level.entities = level.entities.filterNot(e => e.isInstanceOf[PlayerCellEntity] && e.getUUID != player.getUUID)
          case None =>
            if (!isSimulation) throw new IllegalStateException("Cannot start a non-simulation level where no player cell is defined.")
        }

        //create the game engine if needed
        if (engine.isEmpty) engine = Some(GameEngine())
        //initialize the engine with the chosen level data and the level context
        engine.get.init(level, levelContext)
        //signal ui to start the game
        levelContext.setupLevel(level.levelMap.mapShape)
      case None =>
        throw new IllegalArgumentException(s"Unable to load level '$chosenLevel', because its definition was not found.")
    })
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
    //End game result of the multiplayer levels doesn't influence campaign statistics
    lastLoadedLevel = None
    //load level definition
    val loadedLevel = LevelFileManager.getLevelFromResource(levelInfo.name, isMultiPlayer = true).get

    multiPlayerMode.get match {
      case MultiPlayerMode.Server =>
        //assign clients to players and wait confirmation
        server.get.initGame(loadedLevel).future onComplete {
          case Success(_) =>
            //create the engine
            if (engine.isEmpty) engine = Some(GameEngine(Constants.MultiPlayer.defaultMultiPlayerFps))
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

  override def changeLevelSpeed(increment: Boolean = false): Unit = {
    Logger.log("changeLevelSpeed")

    multiPlayerMode match {
      case Some(MultiPlayerMode.Server) | None => if (engine.isDefined) engine.get.changeSpeed(increment)
      case _ =>
    }
  }

  override def stopLevel(victory: Boolean = false): Unit = {
    Logger.log(s"stopLevel - victory: $victory")

    multiPlayerMode match {
      case Some(MultiPlayerMode.Client) =>
        if (client.isDefined) client.get.leaveGame()
      case _ =>

        if (server.isDefined && server.get.getState == ServerState.Game) server.get.stopGame()
        if (engine.isDefined) engine.get.stop()

        saveProgress(if (victory) GameWon else GameLost)
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

  override def getSoundPath(soundType: SoundsType.Value): Option[String] = soundType match {
    case SoundsType.menu => Some(SoundFileManager.loadMenuMusic())
    case SoundsType.level => Some(SoundFileManager.loadLevelMusic())
    case SoundsType.button => Some(SoundFileManager.loadButtonsSound())
    case _ => Logger.log("Sound type not managed!! [getSoundPath]")
              None
  }

  override def getCustomLevels: List[LevelInfo] = LevelFileManager.getCustomLevelsInfo match {
    case Success(customLevels) => customLevels
    case Failure(_) => Logger.log("[Info] User doesn't have any saved custom level or custom level directory doesn't exists")
                               List()
  }

  override def saveLevel(name: String, map: MapShape, victoryRules: VictoryRules.Value, collisionRules: CollisionRules.Value, entities: Seq[CellEntity]): Boolean = {
    val lv: Level = Level(LevelInfo(name, victoryRules) , LevelMap(map, collisionRules), entities.toList)
    lv.checkCellPosition()
    LevelFileManager.saveCustomLevel(lv)
  }

  override def removeLevel(name: String): Boolean = LevelFileManager.deleteCustomLevel(name) match {
    case Success(_) => true
    case Failure(exception) => Logger.log("Error occurred removing custom level file" + exception.getMessage)
                               false
  }

  private def saveProgress(event: GameStateEventWrapper): Unit = lastLoadedLevel match {
    case Some(lastLevel:String) => SinglePlayerLevels.newEndGameEvent(event, lastLevel)
      UserProgressFileManager.saveUserProgress(SinglePlayerLevels.getCampaignLevels)
    case _ =>
  }

  private def loadLevel(chosenLevel:String, isCustom:Boolean, isSimulation:Boolean): Option[Level] =
    if (isCustom) {
      /*Because user campaign stats are not influenced by end game results of the custom levels*/
      lastLoadedLevel = None
      LevelFileManager.getCustomLevel(chosenLevel)
    } else {
      val loadedLevel = LevelFileManager.getLevelFromResource(chosenLevel)
      if (isSimulation) lastLoadedLevel = None else lastLoadedLevel = Some(chosenLevel)
      loadedLevel
    }
}

