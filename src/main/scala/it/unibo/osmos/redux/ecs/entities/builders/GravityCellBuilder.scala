package it.unibo.osmos.redux.ecs.entities.builders
import it.unibo.osmos.redux.ecs.components._
import it.unibo.osmos.redux.ecs.entities.{CellEntity, EntityType, GravityCellEntity}

case class GravityCellBuilder() extends CellBuilder {

  private var specificWeight: SpecificWeightComponent = SpecificWeightComponent(1)
  withEntityType(EntityType.Attractive)

  def withSpecificWeight(weight: Double): GravityCellBuilder = {
    this.specificWeight = SpecificWeightComponent(weight)
    this
  }

  def withSpecificWeight(weight: SpecificWeightComponent): GravityCellBuilder = {
    this.specificWeight = weight.copy()
    this
  }

  override def build: GravityCellEntity = {
    val baseCell: CellEntity = super.build
    GravityCellEntity(baseCell, specificWeight)
  }
}
