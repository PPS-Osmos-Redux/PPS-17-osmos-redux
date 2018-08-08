package it.unibo.osmos.redux.main.ecs.entities

import it.unibo.osmos.redux.main.ecs.components.CollidableComponent

trait Collidable extends Property {

  /**
    * Gets the Collidable Component
    *
    * @return the Collidable Component
    */
  def getCollidableComponent: CollidableComponent
}
