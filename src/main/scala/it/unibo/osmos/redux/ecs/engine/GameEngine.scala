package it.unibo.osmos.redux.ecs.engine

import it.unibo.osmos.redux.ecs.entities.EntityManager
import it.unibo.osmos.redux.ecs.systems._
import it.unibo.osmos.redux.multiplayer.client.Client
import it.unibo.osmos.redux.multiplayer.server.Server
import it.unibo.osmos.redux.mvc.model.Level
import it.unibo.osmos.redux.mvc.view.events.GamePending
import it.unibo.osmos.redux.mvc.view.context.LevelContext
import it.unibo.osmos.redux.utils.InputEventQueue

import scala.collection.mutable.ListBuffer

/**
  * Game engine, the game loop manager.
  */
trait GameEngine {

  type GameStatus = GameStatus.Value

  /**
    * Initializes the game loop with the input data that represents the level.
    *
    * @param level The object that contains all level data.
    * @param levelContext The context of the current game level.
    */
  def init(level: Level, levelContext: LevelContext): Unit

  /**
    * Initializes the game loop and server for a multi-player level.
    * @param level The object that contains all level data.
    * @param levelContext The context of the current game level.
    * @param server The server.
    */
  def init(level: Level, levelContext: LevelContext, server: Server)

  /**
    * Initializes the game loop and client for a multi-player level.
    * @param level The object that contains all level data.
    * @param levelContext The context of the current game level.
    * @param client The client.
    */
  def init(level: Level, levelContext: LevelContext, client: Client)

  /**
    * Starts the game loop.
    */
  def start(): Unit

  /**
    * Pauses the game loop.
    */
  def pause(): Unit

  /**
    * Resumes the game loop.
    */
  def resume(): Unit

  /**
    * Stops the game loop.
    */
  def stop(): Unit

  /**
    * Clears all data of the current game loop.
    */
  def clear(): Unit

  /**
    * Gets the current game loop status.
    * @return The current game loop status.
    */
  def getStatus: GameStatus

  /**
    * Returns the current frame rate.
    * @return The frame rate.
    */
  def getFps: Int
}

/**
  * Game engine object companion.
  */
object GameEngine {

  def apply(): GameEngine = GameEngineImpl()

  /**
    * The Game engine class implementation.
    * @param framerate The frame rate of the game.
    */
  private case class GameEngineImpl(private val framerate: Int = 30) extends GameEngine {

    private var gameLoop: Option[GameLoop] = _

    override def init(level: Level, levelContext: LevelContext): Unit = {

      //clear all
      clear()

      //register InputEventStack to the mouse event listener to collect input events
      levelContext.subscribe(e => { InputEventQueue.enqueue(e)})

      //create systems, add to list, the order in this collection is the final system order in the game loop
      val systems = ListBuffer[System]()
      if (!level.isSimulation) systems += InputSystem()
      systems ++= initMainSystems(level, levelContext)
      if (!level.isSimulation) systems += EndGameSystem(levelContext, level.victoryRule)

      //add all entities in the entity manager (systems are subscribed to EntityManager event when created)
      level.entities foreach(EntityManager add _)

      //init the gameloop
      gameLoop = Some(new GameLoop(this, systems.toList))
    }

    override def init(level: Level, levelContext: LevelContext, server: Server): Unit = {

      //clear all
      clear()

      //register InputEventQueue to the mouse event listener to collect input events
      levelContext.subscribe { InputEventQueue enqueue _ }

      //register InputEventQueue to the client input events
      server.subscribeClientInputEvent { InputEventQueue enqueue _ }

      //create systems, add to list, the order in this collection is the final system order in the game loop
      val systems = ListBuffer[System](InputSystem())
      systems ++= initMainSystems(level, levelContext) :+ MultiPlayerSystem(server) :+ EndGameSystem(levelContext, level.victoryRule)

      //add all entities in the entity manager (systems are subscribed to EntityManager event when created)
      level.entities foreach(EntityManager add _)

      //init the gameloop
      gameLoop = Some(new GameLoop(this, systems.toList))
    }

    override def init(level: Level, levelContext: LevelContext, client: Client): Unit = {

      //clear all
      clear()

      //register client to the mouse event listener to send input events to the server
      levelContext.subscribe(e => { client.signalPlayerInput(e) })

      //subscribe to draw entity event and call interface to draw them
      client.subscribeEntityDrawEvent (entities => {
        //TODO: drawablewrapper must have uuid
        /*
        val player = entities find (e => e.uuid == client.getUUID)
        if (player.isEmpty) throw new IllegalArgumentException("Unable to draw entities because the player is not found")
        levelContext.drawEntities(player, entities)
        */
      })

      //subscribe to game status changed event to detect the end of the game
      client.subscribeGameStatusChangedEvent {
        case GamePending => //TODO: game have been started, tell interface to start
        case s => levelContext.notify(s)
      }
    }

    override def start(): Unit = {
      gameLoop match {
        case Some(g) => g.getStatus match {
          case GameStatus.Idle => g.start()
          case _ => throw new IllegalStateException("Unable to start game loop because the current game status is not idle.")
        }
        case None => throw new IllegalStateException("Unable to start game loop because it hasn't been initialized yet.")
      }
    }

    override def pause(): Unit = {
      gameLoop match {
        case Some(g) => g.pause()
        case None => throw new IllegalStateException("Unable to pause game loop because it hasn't been initialized yet")
      }
    }

    override def resume(): Unit = {
      gameLoop match {
        case Some(g) => g.unpause()
        case None => throw new IllegalStateException("Unable to resume game loop because it hasn't been initialized yet")
      }
    }

    override def stop(): Unit = {
      gameLoop match {
        case Some(g) => g.kill()
        case None => throw new IllegalStateException("Unable to stop game loop because it hasn't been initialized yet")
      }
      gameLoop = None
    }

    override def clear(): Unit = {
      EntityManager.clear()
      InputEventQueue.dequeueAll()

      gameLoop match {
        case Some(i) => i.getStatus match {
          case GameStatus.Running | GameStatus.Paused => throw new IllegalStateException("Unable to clear game engine if the game loop is still running or is paused")
          case _ => i.kill()
        }
        case _ => //do nothing if it's not present
      }
      gameLoop = None
    }

    override def getStatus: GameStatus = {
      gameLoop match {
        case Some(g) => g.getStatus
        case None => throw new IllegalStateException("Unable to get game status if the game loop is not present.")
      }
    }

    override def getFps: Int = framerate

    /**
      * Initializes the main game systems
      * @param level The level
      * @param levelContext The level context
      * @return The list of all main systems
      */
    private def initMainSystems(level: Level, levelContext: LevelContext): List[System] = {
      List(SpawnSystem(), GravitySystem(), MovementSystem(level), CollisionSystem(), CellsEliminationSystem(), DrawSystem(levelContext))
    }
  }
}

