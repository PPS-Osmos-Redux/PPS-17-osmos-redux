package it.unibo.osmos.redux.main.ecs.entities

import it.unibo.osmos.redux.main.ecs.components.Position

trait PositionEntity {

  def getPositionComponent: Position
}
