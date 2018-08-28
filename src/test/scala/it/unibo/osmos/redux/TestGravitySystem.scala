package it.unibo.osmos.redux

import it.unibo.osmos.redux.ecs.components._
import it.unibo.osmos.redux.ecs.entities.{CellEntity, EntityManager, EntityType, GravityCellEntity}
import it.unibo.osmos.redux.ecs.systems.GravitySystem
import it.unibo.osmos.redux.utils.Point
import org.scalatest.{BeforeAndAfter, FunSuite}
import org.scalactic.Tolerance._

class TestGravitySystem extends FunSuite with BeforeAndAfter{

  val TOLERANCE = 0.01
  var acceleration = AccelerationComponent(0, 0)
  val collidable = CollidableComponent(true)
  val speed = SpeedComponent(4, 0)
  val dimension = DimensionComponent(3)
  val position = PositionComponent(Point(0, 0))
  val visible = VisibleComponent(true)
  val baseTypeEntity = TypeComponent(EntityType.Matter)
  val attractiveTypeEntity = TypeComponent(EntityType.Attractive)
  val repulseTypeEntity = TypeComponent(EntityType.Repulsive)
  val specificWeight = SpecificWeightComponent(1.5)
  val dimension1 = DimensionComponent(5)
  val position1 = PositionComponent(Point(3, 4))

  val repulseSpecificWeight = SpecificWeightComponent(2)
  val repulseDimension = DimensionComponent(4)
  val repulsePosition = PositionComponent(Point(7,2))

  after{
    EntityManager.clear()
    acceleration = AccelerationComponent(0, 0)
  }

  test("check mass calculation") {
    val gravityCellEntity = GravityCellEntity(acceleration,collidable,dimension,position,speed,visible,attractiveTypeEntity,specificWeight)
    assert(gravityCellEntity.getMassComponent.mass === 42.411 +- TOLERANCE)
  }

  test("Acceleration of CellEntity should not change without GravityCellEntity") {
    val cellEntity = CellEntity(acceleration,collidable,dimension,position,speed,visible,baseTypeEntity)
    val gravitySystem = GravitySystem()
    EntityManager.add(cellEntity)
    val originalAcceleration = AccelerationComponent(cellEntity.getAccelerationComponent.vector.x, cellEntity.getAccelerationComponent.vector.y)
    gravitySystem.update()
    assert(cellEntity.getAccelerationComponent.vector.x === originalAcceleration.vector.x)
  }

  test("Attractive GravityCellEntity should change acceleration of CellEntity to attract") {
    val cellEntity = CellEntity(acceleration,collidable,dimension1,position1,speed,visible,baseTypeEntity)
    val gravity = GravityCellEntity(acceleration,collidable,dimension,position,speed,visible,attractiveTypeEntity,specificWeight)
    val system = GravitySystem()
    EntityManager.add(cellEntity)
    EntityManager.add(gravity)
    system.update()
    assert(cellEntity.getAccelerationComponent.vector.x === -1.017 +- TOLERANCE)
    assert(cellEntity.getAccelerationComponent.vector.y === -1.357 +- TOLERANCE)
  }

  test("Repulse GravityCellEntity should change acceleration of CellEntity to repulse") {
    val cellEntity = CellEntity(acceleration,collidable,dimension1,position1,speed,visible,baseTypeEntity)
    val gravity = GravityCellEntity(acceleration,collidable,repulseDimension,repulsePosition,speed,visible,repulseTypeEntity,repulseSpecificWeight)
    val system = GravitySystem()
    EntityManager.add(cellEntity)
    EntityManager.add(gravity)
    system.update()
    assert(cellEntity.getAccelerationComponent.vector.x === -4.496 +- TOLERANCE)
    assert(cellEntity.getAccelerationComponent.vector.y === 2.248 +- TOLERANCE)
  }

  test("More GravityCellEntity") {
    val cellEntity = CellEntity(acceleration,collidable,dimension1,position1,speed,visible,baseTypeEntity)
    val gravityAttractive = GravityCellEntity(AccelerationComponent(0,0),collidable,dimension,position,speed,visible,attractiveTypeEntity,specificWeight)
    val gravityRepulse = GravityCellEntity(AccelerationComponent(1,1),collidable,repulseDimension,repulsePosition,speed,visible,repulseTypeEntity,repulseSpecificWeight)
    val system = GravitySystem()
    EntityManager.add(cellEntity)
    EntityManager.add(gravityAttractive)
    EntityManager.add(gravityRepulse)
    system.update()
    assert(cellEntity.getAccelerationComponent.vector.x === -5.513 +- TOLERANCE)
    assert(cellEntity.getAccelerationComponent.vector.y === 0.891 +- TOLERANCE)
    assert(gravityAttractive.getAccelerationComponent.vector.x === -1.824 +- TOLERANCE)
    assert(gravityAttractive.getAccelerationComponent.vector.y === -0.521 +- TOLERANCE)
    assert(gravityRepulse.getAccelerationComponent.vector.x === 0.230 +- TOLERANCE)
    assert(gravityRepulse.getAccelerationComponent.vector.y === 0.780 +- TOLERANCE)
  }
}
