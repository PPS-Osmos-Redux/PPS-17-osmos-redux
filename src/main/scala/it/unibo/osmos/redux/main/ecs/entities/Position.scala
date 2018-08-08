package it.unibo.osmos.redux.main.ecs.entities

import it.unibo.osmos.redux.main.ecs.components.PositionComponent

trait Position extends Property {

  /**
    * Gets the Position Component
    *
    * @return the Position Component
    */
  def getPositionComponent: PositionComponent
}
