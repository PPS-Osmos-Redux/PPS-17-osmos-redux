package it.unibo.osmos.redux.main.ecs.components

/**
  * Cartesian point
  */
trait Point {

  /**
    * Getter. Return the x coordinate of the point
    * @return x coordinate
    */
  def x: Int

  /**
    * Getter. Return the y coordinate of the point
    * @return y coordinate
    */
  def y: Int
}

object Point {
  def apply(x: Int, y: Int): Point = new PointImpl(x,y)

  private case class PointImpl(override val x: Int, override val y: Int) extends Point {}
}


