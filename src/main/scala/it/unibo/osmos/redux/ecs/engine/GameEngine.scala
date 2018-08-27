package it.unibo.osmos.redux.ecs.engine

import it.unibo.osmos.redux.ecs.entities.{CellEntity, EntityManager}
import it.unibo.osmos.redux.ecs.systems.{CollisionSystem, DrawSystem, InputSystem, MovementSystem}
import it.unibo.osmos.redux.mvc.model.MapShape.Rectangle
import it.unibo.osmos.redux.mvc.model.{CollisionRules, Level, LevelMap, VictoryRules}
import it.unibo.osmos.redux.ecs.entities.EntityManager
import it.unibo.osmos.redux.ecs.systems._
import it.unibo.osmos.redux.mvc.model.Level
import it.unibo.osmos.redux.mvc.view.levels.LevelContext
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
      systems ++= List(SpawnSystem(), GravitySystem(), MovementSystem(), CollisionSystem(level), CellsEliminationSystem(), SentientSystem(), DrawSystem(levelContext))
      if(!level.isSimulation) systems += EndGameSystem(levelContext, level.victoryRule)

      //add all entities in the entity manager (systems are subscribed to EntityManager event when created)
      level.entities foreach(EntityManager add _)

      //init the gameloop
      gameLoop = Some(new GameLoop(this, systems.toList))
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
  }
}

