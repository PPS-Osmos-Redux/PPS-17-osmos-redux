package it.unibo.osmos.redux.ecs.entities

import java.util.UUID

import it.unibo.osmos.redux.ecs.components._
import it.unibo.osmos.redux.ecs.entities.builders.CellBuilder

/** Trait representing a CellEntity */
trait CellEntity extends AbstractEntity with MovableProperty with CollidableProperty with DrawableProperty with DeathProperty with SentientEnemyProperty {}

object CellEntity {
  def apply(acceleration: AccelerationComponent,
            collidable: CollidableComponent,
            dimension: DimensionComponent,
            position: PositionComponent,
            speed: SpeedComponent,
            visible: VisibleComponent,
            typeEntity: TypeComponent): CellEntity = CellEntityImpl(acceleration, collidable, dimension, position, speed, visible, typeEntity)

  def apply(builder: CellBuilder): CellEntity = {
    val entity = builder.build
    CellEntityImpl(entity.getAccelerationComponent, entity.getCollidableComponent, entity.getDimensionComponent,
      entity.getPositionComponent, entity.getSpeedComponent, entity.getVisibleComponent, entity.getTypeComponent)
  }

  private case class CellEntityImpl(private val acceleration: AccelerationComponent,
                                    private val collidable: CollidableComponent,
                                    private val dimension: DimensionComponent,
                                    private val position: PositionComponent,
                                    private val speed: SpeedComponent,
                                    private val visible: VisibleComponent,
                                    private val typeEntity: TypeComponent) extends CellEntity {

    require(dimension.radius > 0)

    private val EntityUUID: String = UUID.randomUUID().toString

    override def getUUID: String = EntityUUID

    override def getAccelerationComponent: AccelerationComponent = acceleration

    override def getCollidableComponent: CollidableComponent = collidable

    override def getDimensionComponent: DimensionComponent = dimension

    override def getPositionComponent: PositionComponent = position

    override def getSpeedComponent: SpeedComponent = speed

    override def getVisibleComponent: VisibleComponent = visible

    override def getTypeComponent: TypeComponent = typeEntity
  }
}
