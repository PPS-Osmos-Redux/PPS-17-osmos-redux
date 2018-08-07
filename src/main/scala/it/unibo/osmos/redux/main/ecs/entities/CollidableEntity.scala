package it.unibo.osmos.redux.main.ecs.entities

import it.unibo.osmos.redux.main.ecs.components.Collidable

trait CollidableEntity {

  def getCollidableComponent: Collidable
}
