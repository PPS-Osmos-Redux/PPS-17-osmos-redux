package it.unibo.osmos.redux.main.ecs.entities

import it.unibo.osmos.redux.main.ecs.components.Position

trait PositionEntity {

  /**
    * Gets the Position Component
    *
    * @return the Position Component
    */
  def getPositionComponent: Position
}