package it.unibo.osmos.redux.ecs.components

import it.unibo.osmos.redux.utils.{Point, Vector}

trait VectorComponent {

  def vector: Vector

  def vector_(vector: Vector): Unit
/*
  def x: Double

  def y: Double

  def x_(newX: Double): Unit

  def y_(newY: Double): Unit

  def add(v2: VectorComponent): VectorComponent = {
    x_(x + v2.x)
    y_(y + v2.y)
    this
  }

  def subtract(v2: VectorComponent): VectorComponent = {
    x_(x - v2.x)
    y_(y - v2.y)
    this
  }

  def subtract(v2: Point): VectorComponent = {
    x_(x - v2.x)
    y_(y - v2.y)
    this
  }

  def multiply(v2: VectorComponent): VectorComponent = {
    x_(x * v2.x)
    y_(y * v2.y)
    this
  }

  def multiply(v2: Double): VectorComponent = {
    x_(x * v2)
    y_(y * v2)
    this
  }

  def get_length: Double = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2))

  def set_length(new_length: Double): Unit = {
    val oldLength = get_length
    val temp = new_length / oldLength
    x_(x * temp)
    y_(y * temp)
  }

  def distance(v2: VectorComponent): Double = Math.sqrt(Math.pow(x - v2.x, 2) + Math.pow(y - v2.y, 2))

  def dot(v2: VectorComponent): Double = (x * v2.x) + (y * v2.y)

  def normalized(): VectorComponent = {
    val length = get_length

    if (length != 0) {
      x_(x / length)
      y_(y / length)
    }
    this
  }*/
}

/*
object VectorComponent {
  def apply(x: Double, y: Double): VectorComponent = VectorComponentImpl(x, y)

  private case class VectorComponentImpl(var x: Double, var y: Double) extends VectorComponent {

    //override def x: Double = x

    //override def y: Double = y

    //override def add(v2: VectorComponent): VectorComponent = VectorComponent(x + v2.getX, y + v2.getY)

    override def subtract(v2: VectorComponent): VectorComponent = VectorComponent(x - v2.x, y - v2.y)

    override def subtract(v2: Point): VectorComponent = VectorComponent(x - v2.x, y - v2.y)

    override def multiply(v2: VectorComponent): VectorComponent = VectorComponent(x * v2.x, y * v2.y)

    override def multiply(v2: Double): VectorComponent = VectorComponent(x * v2, y * v2)

    override def get_length: Double = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2))

    override def set_length(new_length: Double): Unit = {
      val oldLength = get_length
      val temp = new_length / oldLength
      x = x * temp
      y = y * temp
    }

    override def distance(v2: VectorComponent): Double = Math.sqrt(Math.pow(x - v2.x, 2) + Math.pow(y - v2.y, 2))

    override def dot(v2: VectorComponent): Double = (x * v2.x) + (y * v2.y)

    override def normalized(): VectorComponent = {
      val length = get_length

      if (length != 0) {
        VectorComponent(x / length, y / length)
      } else {
        VectorComponent(x, y)
      }
    }
  }

}*/
