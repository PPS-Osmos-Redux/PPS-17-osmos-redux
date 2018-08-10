package it.unibo.osmos.redux.main.ecs.engine

import it.unibo.osmos.redux.main.ecs.entities.{CellEntity, EntityManager}
import it.unibo.osmos.redux.main.mvc.view.levels.LevelContext

/**
  * Game engine, the game loop manager.
  */
trait GameEngine {

  type GameStatus = GameStatus.Value

  /**
    * Initializes the game loop with the input data that represents the level.
    *
    * @param levelContext The context of the current game level.
    * @param entities The entities in the game level.
    */
  def init(levelContext: LevelContext, entities: List[CellEntity]): Unit

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

    override def init(levelContext: LevelContext, entities: List[CellEntity]): Unit = {

      //clear all
      clear()

      //create systems and add to the list
      val systems = List[System]()

      //TODO: register InputSystem to the levelContext.registerMouseEventListener()
      //TODO: pass levelContext to all systems constructors
      //TODO: sort systems list by priority

      //add all
      entities foreach( e => EntityManager.add(e))

      //init the gameloop
      gameLoop = Some(new GameLoop(this, systems))
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
    }

    override def clear(): Unit = {
      //TODO: uncomment
      //EntityManager.clear()

      gameLoop match {
        case Some(i) => i.getStatus match {
          case GameStatus.Running | GameStatus.Paused => throw new IllegalStateException("Unable to clear game engine if the game loop is still running or is paused")
          case _ => i.kill()
        }
        case _ => //do nothing if it's not present
      }
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

