package it.unibo.osmos.redux.main.ecs.engine

import scala.collection.mutable.ListBuffer

/**
  * Game engine
  */
trait GameEngine {

  type GameStatus = GameStatus.Value

  /**
    * Initializes the game (entities, components, systems)
    */
  def init(): Unit

  /**
    * Starts/Resumes the game.
    */
  def start(): Unit

  /**
    * Pauses the game.
    */
  def pause(): Unit

  /**
    * Resumes the game loop.
    */
  def resume(): Unit

  /**
    * Stops the game.
    */
  def stop(): Unit

  /**
    * Gets the current game loop status.
    * @return
    */
  def getStatus: GameStatus

  /**
    * Returns the current fps.
    * @return
    */
  def getFps: Int
}

/**
  * Game engine implementation
  */
object GameEngine {

  def apply(): GameEngine = GameEngineImpl()

  private case class GameEngineImpl(private val framerate: Int = 30) extends GameEngine {

    private var gameLoop: Option[GameLoop]= _

    override def init(): Unit = {
      //TODO: init systems and entities (distribute entities in the correct systems)
      var systems = ListBuffer[System]()

      gameLoop = Some(new GameLoop(this, systems))
    }

    override def start(): Unit = {
      gameLoop match {
        case Some(g) => g.start()
        case None => println("Warning: unable to start game loop because it hasn't been initialized yet")
      }
    }

    override def pause(): Unit = {
      gameLoop match {
        case Some(g) => g.pause()
        case None => println("Warning: unable to pause game loop because it hasn't been initialized yet")
      }
    }

    override def resume(): Unit = {
      gameLoop match {
        case Some(g) => g.unpause()
        case None => println("Warning: unable to resume game loop because it hasn't been initialized yet")
      }
    }

    override def stop(): Unit = {
      gameLoop match {
        case Some(g) => g.kill()
        case None => println("Warning: unable to stop game loop because it hasn't been initialized yet")
      }
    }

    override def getStatus: GameStatus = {
      gameLoop match {
        case Some(g) => g.getStatus
        case None => GameStatus.Idle
      }
    }

    override def getFps: Int = framerate
  }
}

