package it.unibo.osmos.redux.ecs.components

/**
  * Component of the speed vector
  */
trait SpeedComponent {

  /**
    * Getter. Return the speed of the x coordinate
    * @return the speed
    */
  def speedX: Double

  /**
    * Getter. Return the speed of the y coordinate
    * @return the speed
    */
  def speedY: Double

  /**
    * Setter. Set the new speed of the x coordinate
    * @param speed the new speed
    */
  def speedX_(speed: Double): Unit

  /**
    * Setter. Set the new speed of the y coordinate
    * @param speed the new speed
    */
  def speedY_(speed: Double): Unit
}

object SpeedComponent {
  def apply(speedX: Double, speedY: Double): SpeedComponent = SpeedComponentImpl(speedX,speedY)

  private case class SpeedComponentImpl(var _speedX: Double, var _speedY: Double) extends SpeedComponent {
    override def speedX: Double = _speedX

    override def speedY: Double = _speedY

    override def speedX_(speed: Double): Unit = _speedX = speed

    override def speedY_(speed: Double): Unit = _speedY = speed
  }
}
