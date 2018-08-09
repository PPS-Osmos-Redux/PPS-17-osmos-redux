package it.unibo.osmos.redux.main.ecs.entities

import it.unibo.osmos.redux.main.ecs.components.TypeComponent

/**
  * Trait representing the entity's type property
  */
trait Type extends Property{

  /**
    * Gets the Type Component
    *
    * @return the Type Component
    */
  def getTypeComponent: TypeComponent
}
