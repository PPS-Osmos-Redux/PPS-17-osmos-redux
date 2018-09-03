package it.unibo.osmos.redux.ecs.entities

import it.unibo.osmos.redux.ecs.components._
import it.unibo.osmos.redux.ecs.entities.builders.CellBuilder

/** Trait representing a sentient cell */
trait SentientCellEntity extends CellEntity with SentientProperty {}

object SentientCellEntity {
  def apply(acceleration: AccelerationComponent,
            collidable: CollidableComponent,
            dimension: DimensionComponent,
            position: PositionComponent,
            speed: SpeedComponent,
            visible: VisibleComponent,
            spawner: SpawnerComponent,
            typeEntity: TypeComponent = TypeComponent(EntityType.Sentient)): SentientCellEntity =
    SentientCellEntityImpl(CellEntity(acceleration, collidable, dimension, position, speed, visible, typeEntity), spawner)

  def apply(cell: CellEntity, spawner: SpawnerComponent): SentientCellEntity = SentientCellEntityImpl(cell, spawner)

  def apply(builder: CellBuilder, spawner: SpawnerComponent): SentientCellEntity = apply(builder.build, spawner)


  private case class SentientCellEntityImpl(cellEntity: CellEntity, spawner: SpawnerComponent) extends SentientCellEntity {

    override def getUUID: String = cellEntity.getUUID

    override def getAccelerationComponent: AccelerationComponent = cellEntity.getAccelerationComponent

    override def getSpeedComponent: SpeedComponent = cellEntity.getSpeedComponent

    override def getCollidableComponent: CollidableComponent = cellEntity.getCollidableComponent

    override def getTypeComponent: TypeComponent = cellEntity.getTypeComponent

    override def getPositionComponent: PositionComponent = cellEntity.getPositionComponent

    override def getVisibleComponent: VisibleComponent = cellEntity.getVisibleComponent

    override def getDimensionComponent: DimensionComponent = cellEntity.getDimensionComponent

    override def getSpawnerComponent: SpawnerComponent = spawner
  }

}
