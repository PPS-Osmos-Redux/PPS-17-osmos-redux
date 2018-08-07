package it.unibo.osmos.redux.main.ecs.entities

import it.unibo.osmos.redux.main.ecs.components.Acceleration

trait AccelerationEntity {

  def getAccelerationComponent: Acceleration
}
