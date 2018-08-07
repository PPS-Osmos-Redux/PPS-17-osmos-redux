package it.unibo.osmos.redux.main.ecs.entities

import it.unibo.osmos.redux.main.ecs.components.Dimension

trait DimensionEntity {

  def getDimensionComponent: Dimension
}
