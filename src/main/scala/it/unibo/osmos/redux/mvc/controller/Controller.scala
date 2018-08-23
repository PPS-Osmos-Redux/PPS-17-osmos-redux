package it.unibo.osmos.redux.mvc.controller
import it.unibo.osmos.redux.ecs.engine.GameEngine
import it.unibo.osmos.redux.multiplayer.client.Client
import it.unibo.osmos.redux.multiplayer.common.{ActorSystemHolder, MultiPlayerMode}
import it.unibo.osmos.redux.multiplayer.server.Server
import it.unibo.osmos.redux.mvc.model.CampaignLevels
import it.unibo.osmos.redux.mvc.view.levels.LevelContext

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Promise
import scala.util.{Failure, Success}

/**
  * Controller base trait
  */
trait Controller {
  type MultiPlayerMode = MultiPlayerMode.Value

  /**
    * Initializes the level and the game engine.
    * @param levelContext The level context.
    * @param chosenLevel The index of the chosen level.
    * @param isSimulation If the level needs to be initialized as simulation.
    */
  def initLevel(levelContext: LevelContext, chosenLevel: Int, isSimulation: Boolean): Unit

  /**
    * Initializes the multi-player lobby and the server or client.
    * @param config The multi-player config
    * @return Promise that completes with true if the lobby is initialized successfully; otherwise false.
    */
  def initLobby(config: Any): Promise[Boolean]

  /**
    * Initializes the multi-player level and the game engine.
    * @param levelContext The level context.
    * @param chosenLevel The index of the chosen level
    * @return Promise that completes with true if the level is initialized successfully; otherwise false.
    */
  def initMultiPlayerLevel(levelContext: LevelContext, chosenLevel:Int): Promise[Boolean]

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
    * Gets all the levels in the campaign.
    * @return The list of tuples that indicates for each level index if it has been completed.
    */
  def getCampaignLevels: List[(Int,Boolean)] = CampaignLevels.levels.toList
}

case class ControllerImpl() extends Controller {
  private var engine: Option[GameEngine] = None

  //multiplayer variables
  private var multiPlayerMode: Option[MultiPlayerMode] = None
  private var server: Option[Server] = None
  private var client: Option[Client] = None

  override def initLevel(levelContext: LevelContext, chosenLevel: Int, isSimulation: Boolean): Unit = {

    val loadedLevel = FileManager.loadResource(isSimulation, chosenLevel).get
    if (isSimulation) loadedLevel.isSimulation = true
    if(engine.isEmpty) engine = Some(GameEngine())
    engine.get.init(loadedLevel, levelContext)
    levelContext.setupLevel(loadedLevel.levelMap.mapShape)
  }

  override def initLobby(config: Any): Promise[Boolean] = {
    val promise = Promise[Boolean]()

    //DEBUG ONLY
    multiPlayerMode = Some(MultiPlayerMode.Server) //0: Server, 1: Client
    val address: String = "192.168.1.7"
    val port: Int = 2552
    val username: String = "pippo"
    //DEBUG ONLY

    //TODO: Set multiplayerMode according to input data
    //multiplayerMode = Some(config.Mode)
    multiPlayerMode match {
      case Some(MultiPlayerMode.Server) =>

        //initialize the server and creates the lobby
        val server = Server(username)
        server.bind(ActorSystemHolder.createActor(server))
        server.createLobby()

        this.server = Some(server)
        promise.success(true)

      case Some(MultiPlayerMode.Client) =>

        //initialize the client, connects to the server and enters the lobby
        val client = Client()
        client.bind(ActorSystemHolder.createActor(client))
        client.connect(address, port).future andThen {
          case Success(true) => client.enterLobby(username).future
          case Success(false) => false
          case Failure(e: Throwable) => e
        } andThen {
          case Success(true) => this.client = Some(client); promise success true
          case Success(false) => promise success false
          case Failure(e: Throwable) => promise failure e
        }
      case _ =>
        promise failure new IllegalArgumentException("Cannot initialize the lobby if the multi-player mode is not defined")
    }
    promise
  }

  override def initMultiPlayerLevel(levelContext: LevelContext, chosenLevel:Int): Promise[Boolean] = {
    val promise = Promise[Boolean]()

    //load level definition
    //TODO: load multi-player definition
    val loadedLevel = FileManager.loadResource(isSimulation = false, chosenLevel).get

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

            promise success true

          //TODO: change scene to game

          case Failure(_) => promise failure _
        }

      case MultiPlayerMode.Client =>

        //creates and initialize the engine
        if (engine.isEmpty) engine = Some(GameEngine())
        engine.get.init(loadedLevel, levelContext, client.get)

        //signal interface that the engine and servers are ready
        levelContext.setupLevel(loadedLevel.levelMap.mapShape)

        promise success true

      case _ => promise failure new IllegalStateException("Unable to initialize multi-player level if no lobby have been created.")
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
}
