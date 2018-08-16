package it.unibo.osmos.redux.ecs.systems

/**
  * Base trait which represent a system
  */
trait System {

  /**
    * Performs an action on all the entities of the system
    */
  def update(): Unit
}
