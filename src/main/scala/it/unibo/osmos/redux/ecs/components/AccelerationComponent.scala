package it.unibo.osmos.redux.ecs.components

/**
  * Component of the acceleration vector
  */
trait AccelerationComponent {

  /**
    * Getter. Return the acceleration of the x coordinate
    * @return the acceleration
    */
  def accelerationX: Double

  /**
    * Getter. Return the acceleration of the y coordinate
    * @return the acceleration
    */
  def accelerationY: Double

  /**
    * Setter. Set the new acceleration of the x coordinate
    * @param acceleration the new acceleration
    */
  def accelerationX_(acceleration: Double): Unit

  /**
    * Setter. Set the new acceleration of the y coordinate
    * @param acceleration the new acceleration
    */
  def accelerationY_(acceleration: Double): Unit
}

object AccelerationComponent {
  def apply(accelerationX: Double, accelerationY: Double): AccelerationComponent = AccelerationComponentImpl(accelerationX,accelerationY)

  private case class AccelerationComponentImpl(var _accelerationX: Double, var _accelerationY: Double) extends AccelerationComponent {
    override def accelerationX: Double = _accelerationX

    override def accelerationY: Double = _accelerationY

    override def accelerationX_(acceleration: Double): Unit = _accelerationX = acceleration

    override def accelerationY_(acceleration: Double): Unit = _accelerationY = acceleration
  }
}
