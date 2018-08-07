package it.unibo.osmos.redux.main.ecs.entities
import java.util.UUID

import it.unibo.osmos.redux.main.ecs.components._

trait PlayerCellEntity extends CellEntity with InputEntity {

}

object PlayerCellEntity {
  def apply(acceleration: Acceleration,
            collidable: Collidable,
            dimension: Dimension,
            position: Position,
            speed: Speed,
            visible: Visible): PlayerCellEntity = PlayerCellEntityImpl(CellEntity(acceleration, collidable, dimension, position, speed, visible))

  private case class PlayerCellEntityImpl(cellEntity: CellEntity) extends PlayerCellEntity {

    override def getUUID: UUID = cellEntity.getUUID

    override def getAccelerationComponent: Acceleration = cellEntity.getAccelerationComponent

    override def getCollidableComponent: Collidable = cellEntity.getCollidableComponent

    override def getDimensionComponent: Dimension = cellEntity.getDimensionComponent

    override def getPositionComponent: Position = cellEntity.getPositionComponent

    override def getSpeedComponent: Speed = cellEntity.getSpeedComponent

    override def getVisibleComponent: Visible = cellEntity.getVisibleComponent
  }
}
