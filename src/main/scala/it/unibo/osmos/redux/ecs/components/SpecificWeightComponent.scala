package it.unibo.osmos.redux.ecs.components

/**
  * Component for the specific weight of the entity
  * @param specificWeight specific weight
  */
case class SpecificWeightComponent(specificWeight: Double) extends Component {

  override def copy(): SpecificWeightComponent = SpecificWeightComponent(specificWeight)
}
