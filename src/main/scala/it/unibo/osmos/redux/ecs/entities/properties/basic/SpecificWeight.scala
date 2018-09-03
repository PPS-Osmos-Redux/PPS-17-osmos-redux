package it.unibo.osmos.redux.ecs.entities.properties.basic

import it.unibo.osmos.redux.ecs.components.SpecificWeightComponent

/** Trait representing the entity's specific weight property. */
trait SpecificWeight extends Property {

  /** Gets the SpecificWeight Component
    *
    * @return the SpecificWeight Component
    */
  def getSpecificWeightComponent: SpecificWeightComponent
}
