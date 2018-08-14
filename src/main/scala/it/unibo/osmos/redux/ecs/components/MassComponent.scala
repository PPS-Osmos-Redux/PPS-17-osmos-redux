package it.unibo.osmos.redux.ecs.components

/**
  * Component for entity's mass
  */
trait MassComponent {

  /**
    * Getter. Return the entity's mass
    * @return entity's mass
    */
  def mass: Double
}

object MassComponent {

  def apply(dimension: DimensionComponent, specificWeight: SpecificWeightComponent): MassComponent =
    MassComponentImpl(dimension,specificWeight)

  case class MassComponentImpl(dimension: DimensionComponent, specificWeight: SpecificWeightComponent) extends MassComponent {

    override def mass: Double = dimension.radius * specificWeight.specificWeight
  }
}
