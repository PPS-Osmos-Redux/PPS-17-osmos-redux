package it.unibo.osmos.redux.ecs.entities.properties.basic

import it.unibo.osmos.redux.ecs.components.CollidableComponent

/** Trait representing the entity's collidable property */
trait Collidable extends Property {

  /** Gets the collidable component
    *
    * @return collidable component
    */
  def getCollidableComponent: CollidableComponent
}
