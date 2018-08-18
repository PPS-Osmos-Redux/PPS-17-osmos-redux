package it.unibo.osmos.redux.ecs.components

import it.unibo.osmos.redux.utils.Point

trait VectorComponent {

  def getX: Double

  def getY: Double

  def add(v2: VectorComponent): VectorComponent

  def subtract(v2: VectorComponent): VectorComponent

  def subtract(v2: Point): VectorComponent

  def multiply(v2: VectorComponent): VectorComponent

  def multiply(v2: Double): VectorComponent

  def get_length: Double

  def set_length(new_length: Double): Unit

  def distance(v2: VectorComponent): Double

  def dot(v2: VectorComponent): Double

  def normalized(): VectorComponent

}


object VectorComponent {
  def apply(x: Double, y: Double): VectorComponent = VectorComponentImpl(x, y)

  case class VectorComponentImpl(var x: Double, var y: Double) extends VectorComponent {

    override def getX: Double = x

    override def getY: Double = y

    override def add(v2: VectorComponent): VectorComponent = VectorComponent(x + v2.getX, y + v2.getY)

    override def subtract(v2: VectorComponent): VectorComponent = VectorComponent(x - v2.getX, y - v2.getY)

    override def subtract(v2: Point): VectorComponent = VectorComponent(x - v2.x, y - v2.y)

    override def multiply(v2: VectorComponent): VectorComponent = VectorComponent(x * v2.getX, y * v2.getY)

    override def multiply(v2: Double): VectorComponent = VectorComponent(x * v2, y * v2)

    override def get_length: Double = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2))

    override def set_length(new_length: Double): Unit = {
      val oldLength = get_length
      val temp = new_length / oldLength
      x = getX * temp
      y = getY * temp
    }

    override def distance(v2: VectorComponent): Double = Math.sqrt(Math.pow(x - v2.getX, 2) + Math.pow(y - v2.getY, 2))

    override def dot(v2: VectorComponent): Double = (x * v2.getX) + (y * v2.getY)

    override def normalized(): VectorComponent = {
      val length = get_length

      if (length != 0) {
        VectorComponent(x / length, y / length)
      } else {
        VectorComponent(x, y)
      }
    }
  }

}
