package it.unibo.osmos.redux.ecs.components

import it.unibo.osmos.redux.utils.Vector

/** Component of the acceleration vector */
trait AccelerationComponent extends VectorComponent {

  /** Resets this component vector's components to 0 */
  def reset(): Unit

  /** Makes a defensive copy of this instance.
    *
    * @return The new instance.
    */
  override def copy(): AccelerationComponent = AccelerationComponent(vector.x, vector.y)
}

object AccelerationComponent {
  def apply(accelerationX: Double, accelerationY: Double): AccelerationComponent = AccelerationComponentImpl(Vector(accelerationX, accelerationY))

  def apply(acceleration: Vector): AccelerationComponent = AccelerationComponentImpl(acceleration)

  private case class AccelerationComponentImpl(var _speedVector: Vector) extends AccelerationComponent {

    override def vector: Vector = _speedVector

    override def reset(): Unit = vector_(Vector.zero())

    override def vector_(vector: Vector): Unit = _speedVector = vector
  }

}
