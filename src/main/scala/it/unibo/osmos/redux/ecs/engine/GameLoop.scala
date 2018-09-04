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

  override def run(): Unit = {
    status = GameStatus.Running

    while (!stopFlag) {
      lock.lock() //blocked if paused

      val startTick = System.currentTimeMillis()

      try {
        //let game progress by updating all systems
        systems foreach (_.update())
      } finally {
        tryUnlock()
      }

      //game loop iteration must last exactly tickTime, so sleep if the tickTime hasn't been reached yet
      val execTime = System.currentTimeMillis() - startTick

      //println(s"Execution time: $execTime ms")

      if (!stopFlag) {
        val tickTime = getTickTime
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
            case t: Throwable => println(t.getMessage)
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

    if (status != GameStatus.Running) throw new IllegalStateException("Cannot pause the game if it's not running.")

    lock.lock()
    status = GameStatus.Paused
  }

  /**
    * Resumes the execution.
    */
  def unpause(): Unit = {

    if (status != GameStatus.Paused) throw new IllegalStateException("Cannot unpause the game if it's not paused.")

    tryUnlock()
    status = GameStatus.Running
  }

  /**
    * Kills the execution.
    */
  def kill(): Unit = {
    tryUnlock()
    stopFlag = true
  }

  /**
    * Gets the current status.
    * @return The current game status
    */
  def getStatus: GameStatus = status

  /**
    * Gets the current tick time.
    * @return The current tick time in milliseconds.
    */
  private def getTickTime: Int = 1000 / engine.getFps

  /**
    * Tries to unlock the lock, if it fails it does not halt the game.
    */
  private def tryUnlock(): Unit = {
    try {
      if (lock.isLocked) lock.unlock()
    } catch {
      case _: Throwable => //do nothing
    }
  }
}

/**
  * Custom exception to handle game loop tick time overrun
  * @param message The message of the exception (optional)
  * @param cause The cause of the exception (optional)
  */
final case class ExceededTickTimeException(private val message: String = "", private val cause: Throwable = null)
  extends Exception(message, cause)


