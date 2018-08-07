package it.unibo.osmos.redux.main.ecs.components

/**
  * Component Dimension (radius of the sphere)
  */
trait Dimension {

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

object Dimension {
  def apply(radius: Int): Dimension = new DimensionImpl(radius)

  private case class DimensionImpl(var _radius: Int) extends Dimension {
    override def radius: Int = _radius

    override def radius_(radius: Int): Unit = _radius = radius
  }
}
