package it.unibo.osmos.redux.ecs.engine

import java.util.concurrent.locks.ReentrantLock

import it.unibo.osmos.redux.ecs.systems._

/**
  * Implementation of the game loop.
  * @param engine The Game engine.
  * @param systems The list of the systems of the game.
  */
class GameLoop(val engine: GameEngine, var systems: List[System]) extends Thread {

  type GameStatus = GameStatus.Value

  private val lock: ReentrantLock = new ReentrantLock()
  private var status: GameStatus = GameStatus.Idle
  private var stopFlag: Boolean = false
  private val tickTime = 1000 / engine.getFps

  override def run(): Unit = {
    status = GameStatus.Running

    while (!stopFlag) {
      lock.lock() //blocked if paused

      val startTick = System.currentTimeMillis()

      try {
        //let game progress by updating all systems
        systems foreach (_.update())
      } finally {
        lock.unlock()
      }

      //game loop iteration must last exactly tickTime, so sleep if the tickTime hasn't been reached yet
      val execTime = System.currentTimeMillis() - startTick

      //println("Execution time: " + execTime + "ms")

      if (!stopFlag) {
        if (execTime < tickTime) {
          val sleepTime = tickTime - execTime
          try {
            Thread.sleep(sleepTime)
          } catch {
            case _: Throwable => //do nothing
          }
        } else {

          try {
            //TODO: should not block the game, for now catch and print error
            throw ExceededTickTimeException("[Game loop] overrun tick time of " + tickTime +
              "ms (" + engine.getFps + " fps) by " + math.abs(tickTime - execTime) + "ms.")
          } catch {
            case t: Throwable => println(t getMessage)
          }
        }
      }
    }

    status = GameStatus.Stopped
  }

  /**
    * Pauses the execution.
    */
  def pause(): Unit = {
    lock.lock()
    status = GameStatus.Paused
  }

  /**
    * Resumes the execution.
    */
  def unpause(): Unit = {
    lock.unlock()
    status = GameStatus.Running
  }

  /**
    * Kills the execution.
    */
  def kill(): Unit = {
    if (lock.isLocked) lock.unlock()
    stopFlag = true
  }

  /**
    * Gets the current status.
    * @return The current game status
    */
  def getStatus: GameStatus = status
}

/**
  * Custom exception to handle game loop tick time overrun
  * @param message The message of the exception (optional)
  * @param cause The cause of the exception (optional)
  */
final case class ExceededTickTimeException(private val message: String = "", private val cause: Throwable = null)
  extends Exception(message, cause)


