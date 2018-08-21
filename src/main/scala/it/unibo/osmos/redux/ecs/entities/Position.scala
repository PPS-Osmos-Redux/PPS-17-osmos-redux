package it.unibo.osmos.redux.ecs.entities

import it.unibo.osmos.redux.ecs.components.PositionComponent

/** Trait representing the entity's position property */
trait Position extends Property {

  /** Gets the position component
    *
    * @return position component
    */
  def getPositionComponent: PositionComponent
}
