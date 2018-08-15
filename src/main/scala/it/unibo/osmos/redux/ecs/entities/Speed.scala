package it.unibo.osmos.redux.ecs.entities

import it.unibo.osmos.redux.ecs.components.SpeedComponent

/**
  * Trait representing the entity's speed property
  */
trait Speed extends Property {

  /**
    * Gets the Speed Component
    *
    * @return the Speed Component
    */
  def getSpeedComponent: SpeedComponent
}
