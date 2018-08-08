package it.unibo.osmos.redux.main.ecs.engine

import java.util.concurrent.locks.{Lock, ReentrantLock}

import scala.collection.mutable

/**
  * Implementation of the game loop.
  * @param engine The Game engine.
  * @param systems The list of the systems of the game.
  */
class GameLoop(val engine: GameEngine, var systems: mutable.ListBuffer[System]) extends Thread {

  type GameStatus = GameStatus.Value

  private var lock: Lock = new ReentrantLock()
  private var status: GameStatus = GameStatus.Idle
  private var stopFlag: Boolean = false
  private val tickTime = 1000 / engine.getFps

  override def run(): Unit = {
    status = GameStatus.Running

    while (!stopFlag) {
      lock.lock() //blocked if paused

      val startTick = System.currentTimeMillis()

      try {

        //TODO: call update methods for all systems (sort by priority before do that)

      } catch {
        case e: Throwable =>
          println("Error occurred inside gameloop:")
          e.printStackTrace()
      } finally {
        lock.unlock()
      }

      //game loop iteration must last exactly tickTime, so sleep if the tickTime hasn't been reached yet
      val execTime = System.currentTimeMillis() - startTick
      if (execTime < tickTime && !stopFlag) {
        val sleepTime = tickTime - execTime
        try {
          Thread.sleep(sleepTime)
        } catch {
          case _: InterruptedException =>
            println("Gameloop was killed while in sleep")
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
    lock.unlock()
    stopFlag = true
  }

  /**
    * Gets the current status.
    * @return The current game status
    */
  def getStatus: GameStatus = status
}


