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

  private case class CellEntityImpl(entity: Entity,
                                    acceleration: Acceleration,
                                    collidable: Collidable,
                                    dimension: Dimension,
                                    position: Position,
                                    speed: Speed,
                                    visible: Visible) extends CellEntity {

    private val accelerationComponent: Acceleration = acceleration

    private val collidableComponent: Collidable = collidable

    private val dimensionComponent: Dimension = dimension

    private val positionComponent: Position = position

    private val speedComponent: Speed = speed

    private val visibleComponent: Visible = visible

    override def getUUID: UUID = entity.getUUID

    override def getAccelerationComponent: Acceleration = accelerationComponent

    override def getCollidableComponent: Collidable = collidableComponent

    override def getDimensionComponent: Dimension = dimensionComponent

    override def getPositionComponent: Position = positionComponent

    override def getSpeedComponent: Speed = speedComponent

    override def getVisibleComponent: Visible = visibleComponent
  }

}

object TryCellEntity extends App {
  val a = Acceleration(1,1)
  val c = Collidable(true)
  val d = Dimension(5)
  val p = Position(Point(0,0))
  val s = Speed(4,0)
  val v = Visible(true)
  val ce = CellEntity(a,c,d,p,s,v)

  println(ce.getUUID)
  println(ce.getCollidableComponent)
}
