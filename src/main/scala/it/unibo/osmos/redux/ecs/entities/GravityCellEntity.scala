package it.unibo.osmos.redux.ecs.entities
import java.util.UUID

import it.unibo.osmos.redux.ecs.components._

/**
  * Trait representing a CellEntity with gravity force
  */
trait GravityCellEntity extends CellEntity with GravityProperty with SpecificWeight{

}

object GravityCellEntity {

  def apply(acceleration: AccelerationComponent,
            collidable: CollidableComponent,
            dimension: DimensionComponent,
            position: PositionComponent,
            speed: SpeedComponent,
            visible: VisibleComponent,
            typeEntity: TypeComponent,
            specificWeight: SpecificWeightComponent): GravityCellEntity =
    GravityCellEntityImpl(CellEntity(acceleration, collidable, dimension, position, speed, visible, typeEntity),
                          MassComponent(dimension,specificWeight), specificWeight)

  def apply(cell: CellEntity, specificWeight: SpecificWeightComponent): GravityCellEntity =
    GravityCellEntityImpl(cell, MassComponent(cell.getDimensionComponent,specificWeight),specificWeight)

  private case class GravityCellEntityImpl(cellEntity: CellEntity, mass: MassComponent, specificWeight: SpecificWeightComponent) extends GravityCellEntity {

    override def getPositionComponent: PositionComponent = cellEntity.getPositionComponent

    override def getDimensionComponent: DimensionComponent = cellEntity.getDimensionComponent

    override def getCollidableComponent: CollidableComponent = cellEntity.getCollidableComponent

    override def getSpeedComponent: SpeedComponent = cellEntity.getSpeedComponent

    override def getAccelerationComponent: AccelerationComponent = cellEntity.getAccelerationComponent

    override def getTypeComponent: TypeComponent = cellEntity.getTypeComponent

    override def getUUID: UUID = cellEntity.getUUID

    override def getVisibleComponent: VisibleComponent = cellEntity.getVisibleComponent

    override def getMassComponent: MassComponent = mass

    /**
      * Need to JsonProtocol to serialize the specific weight
      * @return the SpecificWeight Component
      */
    override def getSpecificWeightComponent: SpecificWeightComponent = specificWeight
  }
}
