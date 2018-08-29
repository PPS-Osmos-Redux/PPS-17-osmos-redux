package it.unibo.osmos.redux

import it.unibo.osmos.redux.ecs.components._
import it.unibo.osmos.redux.ecs.entities.{CellEntity, EntityManager, EntityType, SentientCellEntity}
import it.unibo.osmos.redux.ecs.systems.SentientSystem
import it.unibo.osmos.redux.utils.Point
import org.scalatest.{BeforeAndAfter, FunSuite}
import org.scalactic.Tolerance._

class TestSentientSystem extends FunSuite with BeforeAndAfter{

  val TOLERANCE = 0.01
  var acceleration = AccelerationComponent(0, 0)
  val collidable = CollidableComponent(true)
  val speed = SpeedComponent(0, 0)
  val dimension = DimensionComponent(8)
  val position = PositionComponent(Point(0, 4))
  val visible = VisibleComponent(true)
  val baseTypeEntity = TypeComponent(EntityType.Matter)
  val sentientTypeEntity = TypeComponent(EntityType.Sentient)
  val dimension1 = DimensionComponent(5)
  val position1 = PositionComponent(Point(18, 4))
  val speed1 = SpeedComponent(4, 0)
  val dimension2 = DimensionComponent(10)
  val position2 = PositionComponent(Point(17, 17))

  after{
    EntityManager.clear()
    acceleration = AccelerationComponent(0, 0)
  }

  test("Acceleration of SentientCellEntity should not change without any target") {
    val sentienCellEntity = SentientCellEntity(acceleration,collidable,dimension,position,speed,visible)
    val sentientSystem = SentientSystem()
    EntityManager.add(sentienCellEntity)
    val originalAcceleration = AccelerationComponent(sentienCellEntity.getAccelerationComponent.vector)
    sentientSystem.update()
    assert(sentienCellEntity.getAccelerationComponent.vector.x === originalAcceleration.vector.x)
    assert(sentienCellEntity.getAccelerationComponent.vector.y === originalAcceleration.vector.y)
  }

  test("Acceleration of SentientCellEntity should change with a target in target's direction") {
    val cellEntity = CellEntity(acceleration,collidable,dimension1,position1,speed1,visible,baseTypeEntity)
    val sentienCellEntity = SentientCellEntity(acceleration,collidable,dimension,position,speed,visible)
    val system = SentientSystem()
    EntityManager.add(cellEntity)
    EntityManager.add(sentienCellEntity)
    system.update()
    assert(sentienCellEntity.getAccelerationComponent.vector.x === 0.1 +- TOLERANCE)
    assert(sentienCellEntity.getAccelerationComponent.vector.y === 0.0 +- TOLERANCE)
  }

  test("Acceleration of SentientCellEntity should change to avoid enemies") {
    val cellEntity = CellEntity(acceleration,collidable,dimension2,position1,speed1,visible,baseTypeEntity)
    val cellEntity1 = CellEntity(acceleration,collidable,dimension2,position2,speed1,visible,baseTypeEntity)
    val sentienCellEntity = SentientCellEntity(acceleration,collidable,dimension,position,speed,visible)
    val system = SentientSystem()
    EntityManager.add(cellEntity)
    EntityManager.add(cellEntity1)
    EntityManager.add(sentienCellEntity)
    system.update()
    assert(sentienCellEntity.getAccelerationComponent.vector.x === -0.094 +- TOLERANCE)
    assert(sentienCellEntity.getAccelerationComponent.vector.y === -0.011 +- TOLERANCE)
  }
}
