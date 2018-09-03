package it.unibo.osmos.redux.ecs.entities.properties.basic

import it.unibo.osmos.redux.ecs.components.MassComponent

/** Trait representing the entity's mass property */
trait Mass extends Property {

  /** Gets the Mass Component
    *
    * @return the Mass Component
    */
  def getMassComponent: MassComponent
}
