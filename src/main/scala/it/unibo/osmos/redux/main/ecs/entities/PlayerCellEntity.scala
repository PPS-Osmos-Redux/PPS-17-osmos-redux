package it.unibo.osmos.redux.main.ecs.entities

import java.util.UUID

import it.unibo.osmos.redux.main.ecs.components._

trait PlayerCellEntity extends CellEntity with InputProperty {

}

object PlayerCellEntity {
  def apply(acceleration: AccelerationComponent,
            collidable: CollidableComponent,
            dimension: DimensionComponent,
            position: PositionComponent,
            speed: SpeedComponent,
            visible: VisibleComponent,
            typeEntity: TypeComponent): PlayerCellEntity = PlayerCellEntityImpl(CellEntity(acceleration, collidable, dimension, position, speed, visible, typeEntity))

  private case class PlayerCellEntityImpl(cellEntity: CellEntity) extends PlayerCellEntity {

    override def getUUID: UUID = cellEntity.getUUID

    override def getAccelerationComponent: AccelerationComponent = cellEntity.getAccelerationComponent

    override def getCollidableComponent: CollidableComponent = cellEntity.getCollidableComponent

    override def getDimensionComponent: DimensionComponent = cellEntity.getDimensionComponent

    override def getPositionComponent: PositionComponent = cellEntity.getPositionComponent

    override def getSpeedComponent: SpeedComponent = cellEntity.getSpeedComponent

    override def getVisibleComponent: VisibleComponent = cellEntity.getVisibleComponent

    override def getTypeComponent: TypeComponent = cellEntity.getTypeComponent
  }

}
