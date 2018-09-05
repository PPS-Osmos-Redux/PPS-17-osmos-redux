package it.unibo.osmos.redux.ecs.entities.builders
import it.unibo.osmos.redux.ecs.components._
import it.unibo.osmos.redux.ecs.entities.{EntityType, GravityCellEntity}

case class GravityCellBuilder() extends EntityBuilder[GravityCellEntity] {

  private var specificWeight: SpecificWeightComponent = SpecificWeightComponent(1)

  withEntityType(EntityType.Attractive)

  /**
    * Sets the specific weight.
    * @param weight The weight.
    * @return The entity builder.
    */
  def withSpecificWeight(weight: Double): GravityCellBuilder = {
    this.specificWeight = SpecificWeightComponent(weight)
    this
  }

  /**
    * Sets the specific weight.
    * @param weight The specific weight component.
    * @return The entity builder.
    */
  def withSpecificWeight(weight: SpecificWeightComponent): GravityCellBuilder = {
    this.specificWeight = weight.copy()
    this
  }

  override def build: GravityCellEntity = {
    GravityCellEntity(buildBaseCell(), specificWeight)
  }
}
