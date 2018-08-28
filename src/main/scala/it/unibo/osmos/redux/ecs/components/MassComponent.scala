package it.unibo.osmos.redux.ecs.components

import it.unibo.osmos.redux.utils.MathUtils

/**
  * Component for entity's mass
  */
trait MassComponent extends Component {

  /**
    * Getter. Return the entity's mass
    * @return entity's mass
    */
  def mass: Double

  /**
    * Makes a defensive copy of this instance.
    * @return The new instance.
    */
  override def copy(): MassComponent
}

object MassComponent {

  def apply(dimension: DimensionComponent, specificWeight: SpecificWeightComponent): MassComponent =
    MassComponentImpl(dimension,specificWeight)

  case class MassComponentImpl(dimension: DimensionComponent, specificWeight: SpecificWeightComponent) extends MassComponent {

    override def mass: Double = MathUtils.circleArea(dimension.radius) * specificWeight.specificWeight

    override def copy(): MassComponent = MassComponent(dimension.copy(), specificWeight.copy())
  }
}
