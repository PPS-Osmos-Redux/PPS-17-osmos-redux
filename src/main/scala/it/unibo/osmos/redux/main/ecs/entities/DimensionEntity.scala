package it.unibo.osmos.redux.main.ecs.entities

import it.unibo.osmos.redux.main.ecs.components.Dimension

trait DimensionEntity {

  /**
    * Gets the Dimension Component
    *
    * @return the Dimension Component
    */
  def getDimensionComponent: Dimension
}
