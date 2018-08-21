package it.unibo.osmos.redux.ecs.engine

object GameStatus extends Enumeration {
  val Idle, Running, Paused, Stopped = Value
}
