package it.unibo.osmos.redux.main.ecs.components

/**
  * Component Position (coordinates of the sphere's center)
  */
trait Position {

  /**
    * Getter. Return the center of the sphere
    * @return the center
    */
  def point: Point

  /**
    * Setter. Set the new center of the speed
    * @param point the new center
    */
  def point_(point: Point): Unit
}

object Position {
  def apply(point: Point): Position = new PositionImpl(point)

  private case class PositionImpl(var _point: Point) extends Position {

    override def point: Point = _point

    override def point_(point: Point): Unit = _point = point
  }
}
