package it.unibo.osmos.redux.main.ecs.entities

import it.unibo.osmos.redux.main.ecs.components.AccelerationComponent

/**
  * Trait representing the entity's acceleration property
  */
trait Acceleration extends Property {

  /**
    * Gets the Acceleration Component
    *
    * @return the Acceleration Component
    */
  def getAccelerationComponent: AccelerationComponent
}
