package it.unibo.osmos.redux.utils

trait Vector {
  def x: Double

  def y: Double

  def x_(newX: Double): Unit

  def y_(newY: Double): Unit

  def add(v2: Vector): Vector = Vector(x + v2.x, y + v2.y)

  def subtract(v2: Vector): Vector = {

    Vector(x - v2.x, y - v2.y)
  }

  def subtract(v2: Point): Vector = {

    Vector(x - v2.x, y - v2.y)
  }

  def multiply(v2: Vector): Vector = {
    Vector(x * v2.x, y * v2.y)
  }

  def multiply(v2: Double): Vector = {
    Vector(x * v2, y * v2)
  }

  def get_length: Double = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2))

  def set_length(new_length: Double): Vector = {
    val oldLength = get_length
    val temp = new_length / oldLength
    Vector(x * temp, y * temp)
  }

  def distance(v2: Vector): Double = Math.sqrt(Math.pow(x - v2.x, 2) + Math.pow(y - v2.y, 2))

  def dot(v2: Vector): Double = (x * v2.x) + (y * v2.y)

  def normalized(): Vector = {
    val length = get_length

    if (length != 0) {
      Vector(x / length, y / length)
    } else {
      Vector(x, y)
    }
  }
}

object Vector {
  def apply(x: Double, y: Double): Vector = VectorImpl(x, y)

  private case class VectorImpl(var _x: Double, var _y: Double) extends Vector {

    override def x: Double = _x

    override def y: Double = _y

    override def x_(newX: Double): Unit = _x = newX

    override def y_(newY: Double): Unit = _y = newY
  }

}
