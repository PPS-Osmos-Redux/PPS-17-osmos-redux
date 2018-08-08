package it.unibo.osmos.redux.main.ecs.entities

import it.unibo.osmos.redux.main.ecs.components.Collidable

trait CollidableEntity {

  /**
    * Gets the Collidable Component
    *
    * @return the Collidable Component
    */
  def getCollidableComponent: Collidable
}
