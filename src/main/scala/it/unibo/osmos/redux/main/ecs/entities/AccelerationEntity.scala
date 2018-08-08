package it.unibo.osmos.redux.main.ecs.entities

import it.unibo.osmos.redux.main.ecs.components.AccelerationComponent

trait AccelerationEntity {

  /**
    * Gets the Acceleration Component
    *
    * @return the Acceleration Component
    */
  def getAccelerationComponent: AccelerationComponent
}
