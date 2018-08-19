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

  /** Point-point subtraction.
    *
    * @param p point to subtract
    * @return subtraction result as a new instance
    */
  def subtract(p: Point): Vector

  /** Point-vector subtraction.
    *
    * @param v vector to subtract
    * @return subtraction result as a new instance
    */
  def subtract(v: Vector): Vector
}

object Point {
  def apply(x: Double, y: Double): Point = PointImpl(x, y)

  private case class PointImpl(override val x: Double, override val y: Double) extends Point {

    override def subtract(p2: Point): Vector = Vector(x - p2.x, y - p2.y)

    override def subtract(p2: Vector): Vector = Vector(x - p2.x, y - p2.y)
  }

}


