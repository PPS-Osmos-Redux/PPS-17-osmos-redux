package it.unibo.osmos.redux.ecs.entities.builders
import it.unibo.osmos.redux.ecs.components._
import it.unibo.osmos.redux.ecs.entities.{CellEntity, GravityCellEntity}

case class GravityCellBuilder() extends CellBuilder {

  private var specificWeight: SpecificWeightComponent = SpecificWeightComponent(1)

  def withSpecificWeight(weight: Double): GravityCellBuilder = {
    this.specificWeight = SpecificWeightComponent(weight)
    this
  }

  override def build: GravityCellEntity = {
    checkMultipleBuild()
    val baseCell: CellEntity = super.build
    GravityCellEntity(baseCell, specificWeight)
  }
}
