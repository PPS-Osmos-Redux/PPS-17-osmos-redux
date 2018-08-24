package it.unibo.osmos.redux.mvc.controller

import it.unibo.osmos.redux.ecs.engine.GameEngine
import it.unibo.osmos.redux.multiplayer.client.Client
import it.unibo.osmos.redux.multiplayer.common.{ActorSystemHolder, MultiPlayerMode}
import it.unibo.osmos.redux.multiplayer.server.Server
import it.unibo.osmos.redux.mvc.model.{Level, MultiPlayerLevels, SinglePlayerLevels}
import it.unibo.osmos.redux.mvc.view.components.multiplayer.User
import it.unibo.osmos.redux.mvc.view.context.{GameStateHolder, LevelContext, LevelContextType, LobbyContext}
import it.unibo.osmos.redux.mvc.view.events._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Promise
import scala.util.{Failure, Success}

trait Observable {
  def subscribe(observer: Observer)
}

trait Observer {
  def notify(event: GameStateEventWrapper, levelContext: GameStateHolder)
}

/**
  * Controller base trait
  */
trait Controller {
  type MultiPlayerMode = MultiPlayerMode.Value
  type LevelContextType = LevelContextType.Value

  /**
    * Initializes the level and the game engine.
    * @param levelContext The level context.
    * @param chosenLevel The index of the chosen level.
    * @param levelContextType The level context type
    */
  def initLevel(levelContext: LevelContext, chosenLevel: Int, levelContextType: LevelContextType.Value): Unit

  /**
    * Initializes the multi-player lobby and the server or client.
    * @param user The user config
    * @param lobbyContext The lobby context
    * @return Promise that completes with true if the lobby is initialized successfully; otherwise false.
    */
  def initLobby(user: User, lobbyContext: LobbyContext): Promise[Boolean]

  /**
    * Initializes the multi-player level and the game engine.
    * @param levelContext The level context.
    * @param chosenLevel The index of the chosen level
    * @return Promise that completes with true if the level is initialized successfully; otherwise false.
    */
  def initMultiPlayerLevel(levelContext: LevelContext, chosenLevel: Int): Promise[Boolean]

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
    * Saves a new custom level.
    * @param customLevel The custom level.
    * @return True, if the operation is successful; otherwise false.
    */
  def saveNewCustomLevel(customLevel:Level): Boolean

  /**
    * Gets all the levels in the campaign.
    * @return The list of tuples that indicates for each level index if it has been completed.
    */
  def getSinglePlayerLevels: List[(String, Boolean)] = SinglePlayerLevels.getLevels

  /**
    * Gets all multi-player levels.
    * @return The list of multi-player levels.
    */
  def getMultiPlayerLevels: List[String] = MultiPlayerLevels.getLevels

  /**
    * Gets all custom levels filename.
    * @return The list of custom levels filename.
    */
  def getCustomLevels: List[String] = FileManager.customLevelsFilesName

}

case class ControllerImpl() extends Controller with Observer {

  private var engine:Option[GameEngine] = None

  //multiplayer variables
  private var multiPlayerMode: Option[MultiPlayerMode] = None
  private var server: Option[Server] = None
  private var client: Option[Client] = None

  override def initLevel(levelContext: LevelContext, chosenLevel: Int, levelContextType: LevelContextType.Value): Unit = {

    var loadedLevel: Option[Level] = FileManager.loadResource(chosenLevel.toString)

    //TODO: support custom levels (maybe add a new parameter or load custom level if the default one is not found)
    //if (isCustomLevel) loadedLevel = FileManager.loadCustomLevel(chosenLevel.toString)

    if (loadedLevel.isDefined) {
      loadedLevel.get.isSimulation = levelContextType == LevelContextType.simulation
      if (engine.isEmpty) engine = Some(GameEngine())
      engine.get.init(loadedLevel.get, levelContext)
      levelContext.setupLevel(loadedLevel.get.levelMap.mapShape)
    } else {
      //println("File ", chosenLevel, " not found! is a custom level? ", isCustomLevel)
    }
  }

  override def initLobby(user: User, lobbyContext: LobbyContext): Promise[Boolean] = {
    val promise = Promise[Boolean]()

    multiPlayerMode = Some(if (user.isServer) MultiPlayerMode.Server else MultiPlayerMode.Client)

    multiPlayerMode match {
      case Some(MultiPlayerMode.Server) =>

        //initialize the server and creates the lobby
        val server = Server(user.username)
        server.bind(ActorSystemHolder.createActor(server))
        server.createLobby()

        //TODO: use lobby context to send updates about lobby players

        this.server = Some(server)
        promise.success(true)

      case Some(MultiPlayerMode.Client) =>

        //initialize the client, connects to the server and enters the lobby
        val client = Client()
        client.bind(ActorSystemHolder.createActor(client))
        client.connect(user.ip, user.port.toInt).future andThen {
          case Success(true) => client.enterLobby(user.username).future
          case Success(false) => false
        } andThen {
          case Success(true) =>
            this.client = Some(client)

            //TODO: use lobby context to send updates about lobby players

            promise success true
          case Success(false) => promise success false
        }
      case _ =>
        promise failure new IllegalArgumentException("Cannot initialize the lobby if the multi-player mode is not defined")
    }
    promise
  }

  override def initMultiPlayerLevel(levelContext: LevelContext, chosenLevel: Int): Promise[Boolean] = {
    val promise = Promise[Boolean]()

    //load level definition
    //TODO: load multi-player definition
    val loadedLevel = FileManager.loadResource(chosenLevel.toString, isMultiPlayer = true).get

    multiPlayerMode.get match {
      case MultiPlayerMode.Server =>
        //assign clients to players and wait confirmation
        server.get.startGame(loadedLevel).future onComplete {
          case Success(_) =>
            //creates and initialize the engine
            if (engine.isEmpty) engine = Some(GameEngine())
            engine.get.init(loadedLevel, levelContext, server.get)
            //signal interface that the engine and servers are ready
            levelContext.setupLevel(loadedLevel.levelMap.mapShape)
            //fulfill the promise
            promise success true
          case Failure(_) => promise failure _
          //TODO: change scene to game
        }
      case MultiPlayerMode.Client =>
        //creates and initialize the engine
        if (engine.isEmpty) engine = Some(GameEngine())
        engine.get.init(loadedLevel, levelContext, client.get)
        //signal interface that the engine and servers are ready
        levelContext.setupLevel(loadedLevel.levelMap.mapShape)
        //fulfill the promise
        promise success true
      case _ =>
        promise failure new IllegalStateException("Unable to initialize multi-player level if no lobby have been created.")
    }
    promise
  }

  override def startLevel(): Unit = {
    multiPlayerMode match {
      case Some(MultiPlayerMode.Server) | None => if (engine.isDefined) engine.get.start()
      case _ =>
    }
  }

  override def stopLevel(): Unit = {
    multiPlayerMode match {
      case Some(MultiPlayerMode.Client) => client.get.leaveGame()
      case _ =>
        if (server.isDefined) server.get.stopGame()
        if (engine.isDefined) engine.get.stop()
    }
  }

  override def pauseLevel(): Unit = {
    multiPlayerMode match {
      case None => if (engine.isDefined) engine.get.pause()
      case _ => throw new UnsupportedOperationException("A multi-player level cannot be paused.")
    }
  }

  override def resumeLevel(): Unit = {
    multiPlayerMode match {
      case None => if (engine.isDefined) engine.get.resume()
      case _ => throw new UnsupportedOperationException("A multi-player level cannot be resumed.")
    }
  }

  override def notify(event: GameStateEventWrapper, levelContext: GameStateHolder): Unit = {
    if(event.equals(GameWon)) {
      SinglePlayerLevels.unlockNextLevel()
      FileManager.saveUserProgress(SinglePlayerLevels.toUserProgression)
    }
    levelContext.notify(event)
  }

  override def saveNewCustomLevel(customLevel: Level): Boolean =
    FileManager.saveLevel(customLevel, customLevel.levelId).isDefined

}
