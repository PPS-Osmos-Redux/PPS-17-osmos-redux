package it.unibo.osmos.redux.main.ecs.components

/**
  * Component Dimension (radius of the sphere)
  */
trait DimensionComponent {

  /**
    * Getter. Return the radius of the sphere
    * @return the radius
    */
  def radius: Double

  /**
    * Setter. Set the new value of the radius
    * @param radius the new radius
    */
  def radius_(radius: Double): Unit
}

object DimensionComponent {
  def apply(radius: Double): DimensionComponent = DimensionComponentImpl(radius)

  private case class DimensionComponentImpl(var _radius: Double) extends DimensionComponent {
    override def radius: Double = _radius

    override def radius_(radius: Double): Unit = _radius = radius
  }
}
