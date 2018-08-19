package it.unibo.osmos.redux.ecs.components

import it.unibo.osmos.redux.utils.Vector

/**
  *
  */
trait VectorComponent {

  /**
    *
    * @return
    */
  def vector: Vector

  /**
    *
    * @param vector
    */
  def vector_(vector: Vector): Unit
}
