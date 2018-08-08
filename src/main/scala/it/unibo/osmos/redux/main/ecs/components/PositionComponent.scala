package it.unibo.osmos.redux.main.ecs.components

import it.unibo.osmos.redux.main.utils.Point

/**
  * Component Position (coordinates of the sphere's center)
  */
trait PositionComponent {

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

object PositionComponent {
  def apply(point: Point): PositionComponent = new PositionComponentImpl(point)

  private case class PositionComponentImpl(var _point: Point) extends PositionComponent {

    override def point: Point = _point

    override def point_(point: Point): Unit = _point = point
  }
}
