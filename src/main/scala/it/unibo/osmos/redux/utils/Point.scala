package it.unibo.osmos.redux.utils

/**
  * Cartesian point
  */
trait Point {

  /**
    * Getter. Return the x coordinate of the point
    * @return x coordinate
    */
  def x: Double

  /**
    * Getter. Return the y coordinate of the point
    * @return y coordinate
    */
  def y: Double
}

object Point {
  def apply(x: Double, y: Double): Point = PointImpl(x,y)

  private case class PointImpl(override val x: Double, override val y: Double) extends Point {}
}


