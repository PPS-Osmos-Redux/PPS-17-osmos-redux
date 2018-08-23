package it.unibo.osmos.redux.ecs.entities

import it.unibo.osmos.redux.ecs.components.DimensionComponent

/** Trait representing the entity's dimension property */
trait Dimension extends Property {

  /** Gets the dimension component
    *
    * @return dimension component
    */
  def getDimensionComponent: DimensionComponent
}
