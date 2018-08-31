package it.unibo.osmos.redux.ecs.components

import it.unibo.osmos.redux.utils.Vector

/**
  * Component of the speed vector
  */
trait SpeedComponent extends VectorComponent {

  /**
    * Makes a defensive copy of this instance.
    * @return The new instance.
    */
  override def copy(): SpeedComponent = SpeedComponent(vector.x, vector.y)
}

object SpeedComponent {
  def apply(speedX: Double, speedY: Double): SpeedComponent = SpeedComponentImpl(Vector(speedX, speedY))

  def apply(speed: Vector): SpeedComponent = SpeedComponentImpl(speed)

  private case class SpeedComponentImpl(var _speedVector: Vector) extends SpeedComponent {
    override def vector: Vector = _speedVector

    override def vector_(vector: Vector): Unit = _speedVector = vector
  }
}
