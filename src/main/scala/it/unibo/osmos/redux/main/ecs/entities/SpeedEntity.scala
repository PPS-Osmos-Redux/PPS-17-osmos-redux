package it.unibo.osmos.redux.main.ecs.entities

import it.unibo.osmos.redux.main.ecs.components.SpeedComponent

trait SpeedEntity {

  /**
    * Gets the Speed Component
    *
    * @return the Speed Component
    */
  def getSpeedComponent: SpeedComponent
}
