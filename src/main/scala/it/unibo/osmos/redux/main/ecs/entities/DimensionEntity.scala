package it.unibo.osmos.redux.main.ecs.entities

import it.unibo.osmos.redux.main.ecs.components.DimensionComponent

trait DimensionEntity {

  /**
    * Gets the Dimension Component
    *
    * @return the Dimension Component
    */
  def getDimensionComponent: DimensionComponent
}
