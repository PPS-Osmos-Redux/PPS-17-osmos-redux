package it.unibo.osmos.redux.main.ecs.components

/**
  * Component Dimension (radius of the sphere)
  */
trait DimensionComponent {

  /**
    * Getter. Return the radius of the sphere
    * @return the radius
    */
  def radius: Int

  /**
    * Setter. Set the new value of the radius
    * @param radius the new radius
    */
  def radius_(radius: Int): Unit
}

object DimensionComponent {
  def apply(radius: Int): DimensionComponent = new DimensionComponentImpl(radius)

  private case class DimensionComponentImpl(var _radius: Int) extends DimensionComponent {
    override def radius: Int = _radius

    override def radius_(radius: Int): Unit = _radius = radius
  }
}
