package it.unibo.osmos.redux.utils

/** Cartesian point */
trait Point {

  /** Getter. Return the x coordinate of the point.
    *
    * @return x coordinate
    */
  def x: Double

  /** Getter. Return the y coordinate of the point.
    *
    * @return y coordinate
    */
  def y: Double

  /** Point-vector addition.
    *
    * @param v vector to add
    * @return the addition result as a new Point instance
    */
  def add(v: Vector): Point = Point(x + v.x, y + v.y)

  /** Point-vector subtraction.
    *
    * @param v vector to subtract
    * @return the subtraction result as a new Point instance
    */
  def subtract(v: Vector): Point = Point(x - v.x, y - v.y)

  /** Point-point subtraction.
    *
    * @param p point to subtract
    * @return the subtraction result as a new Vector instance
    */
  def subtract(p: Point): Vector = Vector(x - p.x, y - p.y)

  // TODO: remove
  //def multiply(value: Double): Vector = Vector(x * value, y * value)

}

object Point {
  def apply(x: Double, y: Double): Point = PointImpl(x, y)

  private case class PointImpl(override val x: Double, override val y: Double) extends Point {

  }

}


