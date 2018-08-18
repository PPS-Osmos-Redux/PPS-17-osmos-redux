package it.unibo.osmos.redux.ecs.components

import it.unibo.osmos.redux.utils.Point

/**
  * Component of the acceleration vector
  */
trait AccelerationComponent extends VectorComponent {

  /**
    * Getter. Return the acceleration of the x coordinate
    *
    * @return the acceleration
    */
  def accelerationX: Double

  /**
    * Getter. Return the acceleration of the y coordinate
    *
    * @return the acceleration
    */
  def accelerationY: Double

  /**
    * Setter. Set the new acceleration of the x coordinate
    *
    * @param acceleration the new acceleration
    */
  def accelerationX_(acceleration: Double): Unit

  /**
    * Setter. Set the new acceleration of the y coordinate
    *
    * @param acceleration the new acceleration
    */
  def accelerationY_(acceleration: Double): Unit
}

object AccelerationComponent {
  def apply(accelerationX: Double, accelerationY: Double): AccelerationComponent = AccelerationComponentImpl(accelerationX, accelerationY)

  private case class AccelerationComponentImpl(var _accelerationX: Double, var _accelerationY: Double) extends AccelerationComponent {
    override def accelerationX: Double = _accelerationX

    override def accelerationY: Double = _accelerationY

    override def accelerationX_(acceleration: Double): Unit = _accelerationX = acceleration

    override def accelerationY_(acceleration: Double): Unit = _accelerationY = acceleration


    override def getX: Double = _accelerationX

    override def getY: Double = _accelerationY

    override def add(v2: VectorComponent): VectorComponent = AccelerationComponent(getX + v2.getX, getY + v2.getY)

    override def subtract(v2: VectorComponent): VectorComponent = AccelerationComponent(getX - v2.getX, getY - v2.getY)

    override def subtract(v2: Point): VectorComponent = VectorComponent(getX - v2.x, getY - v2.y)

    override def multiply(v2: VectorComponent): VectorComponent = AccelerationComponent(getX * v2.getX, getY * v2.getY)

    override def multiply(v2: Double): VectorComponent = VectorComponent(getX * v2, getY * v2)

    override def get_length: Double = Math.sqrt(Math.pow(getX, 2) + Math.pow(getY, 2))

    override def set_length(new_length: Double): Unit = {
      val oldLength = get_length
      val temp = new_length / oldLength
      accelerationX_(getX * temp)
      accelerationY_(getY * temp)
    }

    override def distance(v2: VectorComponent): Double = Math.sqrt(Math.pow(getX - v2.getX, 2) + Math.pow(getY - v2.getY, 2))

    override def dot(v2: VectorComponent): Double = (getX * v2.getX) + (getY * v2.getY)

    override def normalized(): VectorComponent = {
      val length = get_length

      if (length != 0) {
        AccelerationComponent(getX / length, getY / length)
      } else {
        AccelerationComponent(getX, getY)
      }
    }
  }

}
