package it.unibo.osmos.redux.ecs.entities

import it.unibo.osmos.redux.ecs.components.DimensionComponent

/**
  * Trait representing the entity's dimension property
  */
trait Dimension extends Property {

  /**
    * Gets the Dimension Component
    *
    * @return the Dimension Component
    */
  def getDimensionComponent: DimensionComponent
}
