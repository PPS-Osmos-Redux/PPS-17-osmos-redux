package it.unibo.osmos.redux.main.ecs.components

trait Position {
  def point: Point
  def point_(point: Point): Unit
}

object Position {
  def apply(point: Point): Position = new PositionImpl(point)

  private case class PositionImpl(var _point: Point) extends Position {

    override def point: Point = _point

    override def point_(point: Point): Unit = _point = point
  }
}
