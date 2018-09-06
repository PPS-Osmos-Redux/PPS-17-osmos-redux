package it.unibo.osmos.redux.ecs.entities

import it.unibo.osmos.redux.ecs.components._
import it.unibo.osmos.redux.ecs.entities.builders.CellBuilder
import it.unibo.osmos.redux.ecs.entities.properties.basic.SpecificWeight
import it.unibo.osmos.redux.ecs.entities.properties.composed.GravityProperty

/** Trait representing a CellEntity with gravity force */
trait GravityCellEntity extends CellEntity with GravityProperty with SpecificWeight {}

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
      MassComponent(dimension, specificWeight), specificWeight)

  def apply(cell: CellEntity, specificWeight: SpecificWeightComponent): GravityCellEntity =
    GravityCellEntityImpl(cell, MassComponent(cell.getDimensionComponent, specificWeight), specificWeight)

  def apply(builder: CellBuilder, specificWeight: SpecificWeightComponent): GravityCellEntity =
    apply(builder.buildCellEntity(), specificWeight)

  private case class GravityCellEntityImpl(cellEntity: CellEntity, mass: MassComponent, specificWeight: SpecificWeightComponent) extends GravityCellEntity {

    require(Seq(EntityType.Attractive, EntityType.Repulsive) contains cellEntity.getTypeComponent.typeEntity)
    require(specificWeight.specificWeight > 0)

    override def getPositionComponent: PositionComponent = cellEntity.getPositionComponent

    override def getDimensionComponent: DimensionComponent = cellEntity.getDimensionComponent

    override def getCollidableComponent: CollidableComponent = cellEntity.getCollidableComponent

    override def getSpeedComponent: SpeedComponent = cellEntity.getSpeedComponent

    override def getAccelerationComponent: AccelerationComponent = cellEntity.getAccelerationComponent

    override def getTypeComponent: TypeComponent = cellEntity.getTypeComponent

    override def getUUID: String = cellEntity.getUUID

    override def getVisibleComponent: VisibleComponent = cellEntity.getVisibleComponent

    override def getMassComponent: MassComponent = mass

    override def getSpecificWeightComponent: SpecificWeightComponent = specificWeight
  }
}
