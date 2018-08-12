package it.unibo.osmos.redux.ecs.systems

/**
  * Base trait which represent a system
  */
trait System {

  /**
    * Getter. Return the priority of the system
    * @return the priority
    */
  def priority: Int

  /**
    * Performs an action on all the entities of the system
    */
  def update(): Unit
}
