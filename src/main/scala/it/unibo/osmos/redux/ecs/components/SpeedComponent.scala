package it.unibo.osmos.redux.ecs.components

import it.unibo.osmos.redux.utils.Point

/**
  * Component of the speed vector
  */
trait SpeedComponent extends VectorComponent {

  /**
    * Getter. Return the speed of the _speedX coordinate
    *
    * @return the speed
    */
  def speedX: Double

  /**
    * Getter. Return the speed of the _speedY coordinate
    *
    * @return the speed
    */
  def speedY: Double

  /**
    * Setter. Set the new speed of the _speedX coordinate
    *
    * @param speed the new speed
    */
  def speedX_(speed: Double): Unit

  /**
    * Setter. Set the new speed of the _speedY coordinate
    *
    * @param speed the new speed
    */
  def speedY_(speed: Double): Unit
}

object SpeedComponent {
  def apply(speedX: Double, speedY: Double): SpeedComponent = SpeedComponentImpl(speedX, speedY)

  private case class SpeedComponentImpl(var _speedX: Double, var _speedY: Double) extends SpeedComponent {
    override def speedX: Double = _speedX

    override def speedY: Double = _speedY

    override def speedX_(speed: Double): Unit = _speedX = speed

    override def speedY_(speed: Double): Unit = _speedY = speed


    override def getX: Double = _speedX

    override def getY: Double = _speedY

    override def add(v2: VectorComponent): VectorComponent = SpeedComponent(_speedX + v2.getX, _speedY + v2.getY)

    override def subtract(v2: VectorComponent): VectorComponent = SpeedComponent(_speedX - v2.getX, _speedY - v2.getY)

    override def subtract(v2: Point): VectorComponent = VectorComponent(getX - v2.x, getY - v2.y)

    override def multiply(v2: VectorComponent): VectorComponent = SpeedComponent(_speedX * v2.getX, _speedY * v2.getY)

    override def multiply(v2: Double): VectorComponent = VectorComponent(getX * v2, getX * v2)

    override def get_length: Double = Math.sqrt(Math.pow(_speedX, 2) + Math.pow(_speedY, 2))

    override def set_length(new_length: Double): Unit = {
      val oldLength = get_length
      val temp = new_length / oldLength
      speedX_(getX * temp)
      speedY_(getY * temp)
    }

    override def distance(v2: VectorComponent): Double = Math.sqrt(Math.pow(_speedX - v2.getX, 2) + Math.pow(_speedY - v2.getY, 2))


    override def dot(v2: VectorComponent): Double = (_speedX * v2.getX) + (_speedY * v2.getY)

    override def normalized(): VectorComponent = {
      val length = get_length

      if (length != 0) {
        SpeedComponent(_speedX / length, _speedY / length)
      } else {
        SpeedComponent(_speedX, _speedY)
      }
    }
  }

}
