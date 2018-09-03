package it.unibo.osmos.redux.ecs.entities.properties.basic

import it.unibo.osmos.redux.ecs.components.AccelerationComponent

/** Trait representing the entity's acceleration property */
trait Acceleration extends Property {

  /** Gets the acceleration component
    *
    * @return acceleration component
    */
  def getAccelerationComponent: AccelerationComponent
}
