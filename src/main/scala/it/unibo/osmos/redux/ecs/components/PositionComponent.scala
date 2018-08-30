package it.unibo.osmos.redux.ecs.components

import it.unibo.osmos.redux.utils.Point

/**
  * Component Position (coordinates of the sphere's center)
  */
trait PositionComponent extends Component {

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

  /**
    * Makes a defensive copy of this instance.
    * @return The new instance.
    */
  override def copy(): PositionComponent = PositionComponent(point)
}

object PositionComponent {
  def apply(x: Double, y: Double): PositionComponent = PositionComponentImpl(Point(x, y))

  def apply(point: Point): PositionComponent = PositionComponentImpl(point)

  private case class PositionComponentImpl(var _point: Point) extends PositionComponent {

    override def point: Point = _point

    override def point_(point: Point): Unit = _point = point
  }
}
