package it.unibo.osmos.redux.main.ecs.components

/**
  * Component of the acceleration vector
  */
trait AccelerationComponent {

  /**
    * Getter. Return the acceleration of the x coordinate
    * @return the acceleration
    */
  def accelerationX: Int

  /**
    * Getter. Return the acceleration of the y coordinate
    * @return the acceleration
    */
  def accelerationY: Int

  /**
    * Setter. Set the new acceleration of the x coordinate
    * @param acceleration the new acceleration
    */
  def accelerationX_(acceleration: Int): Unit

  /**
    * Setter. Set the new acceleration of the y coordinate
    * @param acceleration the new acceleration
    */
  def accelerationY_(acceleration: Int): Unit
}

object AccelerationComponent {
  def apply(accelerationX: Int, accelerationY: Int): AccelerationComponent = new AccelerationComponentImpl(accelerationX,accelerationY)

  private case class AccelerationComponentImpl(var _accelerationX: Int, var _accelerationY: Int) extends AccelerationComponent {
    override def accelerationX: Int = _accelerationX

    override def accelerationY: Int = _accelerationY

    override def accelerationX_(acceleration: Int): Unit = _accelerationX = acceleration

    override def accelerationY_(acceleration: Int): Unit = _accelerationY = acceleration
  }
}
