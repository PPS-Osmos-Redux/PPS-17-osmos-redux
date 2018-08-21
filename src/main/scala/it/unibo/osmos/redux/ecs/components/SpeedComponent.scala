package it.unibo.osmos.redux.ecs.components

import it.unibo.osmos.redux.utils

/**
  * Component of the speed vector
  */
trait SpeedComponent extends VectorComponent {

}

object SpeedComponent {
  def apply(speedX: Double, speedY: Double): SpeedComponent = SpeedComponentImpl(utils.Vector(speedX, speedY))

  private case class SpeedComponentImpl(var _speedVector: utils.Vector) extends SpeedComponent {
    override def vector: utils.Vector = _speedVector

    override def vector_(vector: utils.Vector): Unit = _speedVector = vector
  }

}
