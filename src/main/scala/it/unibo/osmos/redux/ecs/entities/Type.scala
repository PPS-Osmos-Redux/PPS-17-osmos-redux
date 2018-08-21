package it.unibo.osmos.redux.ecs.entities

import it.unibo.osmos.redux.ecs.components.TypeComponent

/** Trait representing the entity's type property */
trait Type extends Property {

  /** Gets the Type Component
    *
    * @return the Type Component
    */
  def getTypeComponent: TypeComponent
}
