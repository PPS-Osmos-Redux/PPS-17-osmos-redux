package it.unibo.osmos.redux.main.ecs.entities

import java.util.UUID

import it.unibo.osmos.redux.main.ecs.components._

trait CellEntity extends Entity with MovementEntity with CollisionEntity with DrawableEntity {

}

object CellEntity {
  def apply(acceleration: Acceleration,
            collidable: Collidable,
            dimension: Dimension,
            position: Position,
            speed: Speed,
            visible: Visible): CellEntity = CellEntityImpl(Entity(), acceleration, collidable, dimension, position, speed, visible)

  private case class CellEntityImpl(private val entity: Entity,
                                    private val acceleration: Acceleration,
                                    private val collidable: Collidable,
                                    private val dimension: Dimension,
                                    private val position: Position,
                                    private val speed: Speed,
                                    private val visible: Visible) extends CellEntity {

    override def getUUID: UUID = entity.getUUID

    override def getAccelerationComponent: Acceleration = acceleration

    override def getCollidableComponent: Collidable = collidable

    override def getDimensionComponent: Dimension = dimension

    override def getPositionComponent: Position = position

    override def getSpeedComponent: Speed = speed

    override def getVisibleComponent: Visible = visible
  }

}