package it.unibo.osmos.redux.main.ecs.entities

import it.unibo.osmos.redux.main.ecs.components.Speed

trait SpeedEntity {

  /**
    * Gets the Speed Component
    *
    * @return the Speed Component
    */
  def getSpeedComponent: Speed
}
