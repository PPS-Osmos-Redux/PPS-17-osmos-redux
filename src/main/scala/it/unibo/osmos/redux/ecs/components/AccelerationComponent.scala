package it.unibo.osmos.redux.ecs.components

import it.unibo.osmos.redux.utils.Vector

/** Component of the acceleration vector */
trait AccelerationComponent extends VectorComponent {

  /** Resets this component vector's components to 0 */
  def reset(): Unit
}

object AccelerationComponent {
  def apply(accelerationX: Double, accelerationY: Double): AccelerationComponent = AccelerationComponentImpl(Vector(accelerationX, accelerationY))

  def apply(acceleration: Vector): AccelerationComponent = AccelerationComponentImpl(acceleration)

  private case class AccelerationComponentImpl(var _speedVector: Vector) extends AccelerationComponent {

    override def vector: Vector = _speedVector

    override def vector_(vector: Vector): Unit = _speedVector = vector

    override def reset(): Unit = vector_(Vector.zero())
  }

}
