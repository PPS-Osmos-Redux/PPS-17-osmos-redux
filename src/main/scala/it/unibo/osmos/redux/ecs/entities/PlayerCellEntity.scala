package it.unibo.osmos.redux.ecs.entities

import it.unibo.osmos.redux.ecs.components._
import it.unibo.osmos.redux.ecs.entities.properties.basic.Spawner
import it.unibo.osmos.redux.ecs.entities.properties.composed.InputProperty

/** Trait representing a CellEntity controllable by the player */
trait PlayerCellEntity extends CellEntity with InputProperty with Spawner {}

object PlayerCellEntity {
  def apply(acceleration: AccelerationComponent,
            collidable: CollidableComponent,
            dimension: DimensionComponent,
            position: PositionComponent,
            speed: SpeedComponent,
            visible: VisibleComponent,
            spawner: SpawnerComponent,
            typeEntity: TypeComponent = TypeComponent(EntityType.Controlled)): PlayerCellEntity =
    PlayerCellEntityImpl(CellEntity(acceleration, collidable, dimension, position, speed, visible, typeEntity), spawner)

  def apply(cell: CellEntity, spawner: SpawnerComponent): PlayerCellEntity = PlayerCellEntityImpl(cell, spawner)

  def apply(builder: CellBuilder, spawner: SpawnerComponent): PlayerCellEntity = apply(builder.buildCellEntity(), spawner)

  private case class PlayerCellEntityImpl(cellEntity: CellEntity, private val spawner: SpawnerComponent) extends PlayerCellEntity {

    require(cellEntity.getTypeComponent.typeEntity == EntityType.Controlled)

    override def getUUID: String = cellEntity.getUUID

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
