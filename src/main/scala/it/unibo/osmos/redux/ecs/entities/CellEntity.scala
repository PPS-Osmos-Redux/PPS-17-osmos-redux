package it.unibo.osmos.redux.ecs.entities

import java.util.UUID

import it.unibo.osmos.redux.ecs.components._

/**
  * Trait representing a CellEntity
  */
trait CellEntity extends AbstractEntity with MovableProperty with CollidableProperty with DrawableProperty with DeathProperty{

}

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

    private val EntityUUID: UUID = UUID.randomUUID()

    override def getUUID: UUID = EntityUUID

    override def getAccelerationComponent: AccelerationComponent = acceleration

    override def getCollidableComponent: CollidableComponent = collidable

    override def getDimensionComponent: DimensionComponent = dimension

    override def getPositionComponent: PositionComponent = position

    override def getSpeedComponent: SpeedComponent = speed

    override def getVisibleComponent: VisibleComponent = visible

    override def getTypeComponent: TypeComponent = typeEntity
  }

}
