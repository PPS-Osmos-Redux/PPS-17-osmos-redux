package it.unibo.osmos.redux

import it.unibo.osmos.redux.ecs.components._
import it.unibo.osmos.redux.ecs.entities.{CellEntity, EntityManager, GravityCellEntity}
import it.unibo.osmos.redux.ecs.systems.GravitySystem
import it.unibo.osmos.redux.utils.Point
import org.scalatest.{BeforeAndAfter, FunSuite}
import org.scalactic.Tolerance._

class TestGravitySystem extends FunSuite with BeforeAndAfter{

  val TOLERANCE = 0.03
  val acceleration = AccelerationComponent(0, 0)
  val collidable = CollidableComponent(true)
  val speed = SpeedComponent(4, 0)
  val dimension = DimensionComponent(3)
  val position = PositionComponent(Point(0, 0))
  val visible = VisibleComponent(true)
  val typeEntity = TypeComponent(EntityType.Attractive)
  val specificWeight = SpecificWeightComponent(1.5)

  val dimension1 = DimensionComponent(5)
  val position1 = PositionComponent(Point(3, 4))

  after(EntityManager.clear())

  test("check mass calculation") {
    val gravityCellEntity = GravityCellEntity(acceleration,collidable,dimension,position,speed,visible,typeEntity,specificWeight)
    assert(gravityCellEntity.getMassComponent.mass === 42.39 +- TOLERANCE)
  }

  test("Acceleration of CellEntity should not change without GravityCellEntity") {
    val cellEntity = CellEntity(acceleration,collidable,dimension,position,speed,visible,typeEntity)
    val gravitySystem = GravitySystem(0)
    EntityManager.add(cellEntity)

    val originalAcceleration = AccelerationComponent(cellEntity.getAccelerationComponent.accelerationX, cellEntity.getAccelerationComponent.accelerationY)
    gravitySystem.update()
    assert(cellEntity.getAccelerationComponent.accelerationX === originalAcceleration.accelerationX)
  }

  test("GravityCellEntity should change acceleration of CellEntity") {
    val cellEntity = CellEntity(acceleration,collidable,dimension1,position1,speed,visible,typeEntity)
    val gravity = GravityCellEntity(acceleration,collidable,dimension,position,speed,visible,typeEntity,specificWeight)
    val system = GravitySystem(0)
    EntityManager.add(cellEntity)
    EntityManager.add(gravity)
    system.update()
    assert(cellEntity.getAccelerationComponent.accelerationX === -1.017 +- TOLERANCE)
    assert(cellEntity.getAccelerationComponent.accelerationY === -1.357 +- TOLERANCE)
  }
}
