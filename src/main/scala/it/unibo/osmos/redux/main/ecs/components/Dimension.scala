package it.unibo.osmos.redux.main.ecs.components

trait Dimension {
  def radius: Int
  def radius_(radius: Int): Unit
}

object Dimension {
  def apply(radius: Int): Dimension = new DimensionImpl(radius)

  private case class DimensionImpl(var _radius: Int) extends Dimension {
    override def radius: Int = _radius

    override def radius_(radius: Int): Unit = _radius = radius
  }
}
