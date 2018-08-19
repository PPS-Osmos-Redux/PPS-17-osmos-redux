package it.unibo.osmos.redux.ecs.components

import it.unibo.osmos.redux.utils

/** Component of the acceleration vector */
trait AccelerationComponent extends VectorComponent {

  /** Resets this component vector's components to 0 */
  def reset(): Unit
}

object AccelerationComponent {
  def apply(accelerationX: Double, accelerationY: Double): AccelerationComponent = AccelerationComponentImpl(utils.Vector(accelerationX, accelerationY))

  private case class AccelerationComponentImpl(var _speedVector: utils.Vector) extends AccelerationComponent {

    override def vector: utils.Vector = _speedVector

    override def vector_(vector: utils.Vector): Unit = _speedVector = vector

    override def reset(): Unit = vector_(utils.Vector(0, 0))
  }

}
