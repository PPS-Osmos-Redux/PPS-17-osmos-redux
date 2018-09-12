package it.unibo.osmos.redux.ecs.engine

/** The current status of the game */
object GameStatus extends Enumeration {
  val Idle, Running, Paused, Stopped = Value
}
