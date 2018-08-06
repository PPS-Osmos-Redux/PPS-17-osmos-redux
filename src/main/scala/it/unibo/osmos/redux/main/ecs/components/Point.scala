package it.unibo.osmos.redux.main.ecs.components

trait Point {
  def x: Int
  def y: Int
}

object Point {
  def apply(x: Int, y: Int): Point = new PointImpl(x,y)

  private case class PointImpl(override val x: Int, override val y: Int) extends Point {}
}


