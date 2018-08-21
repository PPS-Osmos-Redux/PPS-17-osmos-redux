package it.unibo.osmos.redux.ecs.entities

import it.unibo.osmos.redux.ecs.components.VisibleComponent

/**
  * Trait representing the entity's visible property
  */
trait Visible extends Property {

  /**
    * Gets the Visible Component
    *
    * @return the Visible Component
    */
  def getVisibleComponent: VisibleComponent
}
