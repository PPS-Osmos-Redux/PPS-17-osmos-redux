package it.unibo.osmos.redux.ecs.entities

import java.util.UUID

import it.unibo.osmos.redux.ecs.components._

/** Trait representing a CellEntity controllable by the player */
trait PlayerCellEntity extends CellEntity with InputProperty with Spawner with SentientTargetProperty {}

object PlayerCellEntity {
  def apply(acceleration: AccelerationComponent,
            collidable: CollidableComponent,
            dimension: DimensionComponent,
            position: PositionComponent,
            speed: SpeedComponent,
            visible: VisibleComponent,
            typeEntity: TypeComponent,
            spawner: SpawnerComponent): PlayerCellEntity = PlayerCellEntityImpl(CellEntity(acceleration,
    collidable, dimension, position, speed, visible, typeEntity), spawner)

  def apply(cell: CellEntity, spawner: SpawnerComponent): PlayerCellEntity = PlayerCellEntityImpl(cell, spawner)

  def apply(builder: CellBuilder, spawner: SpawnerComponent): PlayerCellEntity = PlayerCellEntityImpl(builder.build, spawner)

  private case class PlayerCellEntityImpl(cellEntity: CellEntity, private val spawner: SpawnerComponent) extends PlayerCellEntity {

    override def getUUID: UUID = cellEntity.getUUID

    override def getAccelerationComponent: AccelerationComponent = cellEntity.getAccelerationComponent

    override def getCollidableComponent: CollidableComponent = cellEntity.getCollidableComponent

    override def getDimensionComponent: DimensionComponent = cellEntity.getDimensionComponent

    override def getPositionComponent: PositionComponent = cellEntity.getPositionComponent

    override def getSpeedComponent: SpeedComponent = cellEntity.getSpeedComponent

    override def getVisibleComponent: VisibleComponent = cellEntity.getVisibleComponent

    override def getTypeComponent: TypeComponent = cellEntity.getTypeComponent

    override def getSpawnerComponent: SpawnerComponent = spawner
  }

}
