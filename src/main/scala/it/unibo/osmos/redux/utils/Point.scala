package it.unibo.osmos.redux.utils

import it.unibo.osmos.redux.ecs.components.VectorComponent

/**
  * Cartesian point
  */
trait Point {

  /**
    * Getter. Return the x coordinate of the point
    *
    * @return x coordinate
    */
  def x: Double

  /**
    * Getter. Return the y coordinate of the point
    *
    * @return y coordinate
    */
  def y: Double

  def subtract(p2: Point): VectorComponent

  def subtract(p2: VectorComponent): VectorComponent
}

object Point {
  def apply(x: Double, y: Double): Point = PointImpl(x, y)

  private case class PointImpl(override val x: Double, override val y: Double) extends Point {

    override def subtract(p2: Point): VectorComponent = VectorComponent(x - p2.x, y - p2.y)

    override def subtract(p2: VectorComponent): VectorComponent = VectorComponent(x - p2.getX, y - p2.getY)
  }

}


