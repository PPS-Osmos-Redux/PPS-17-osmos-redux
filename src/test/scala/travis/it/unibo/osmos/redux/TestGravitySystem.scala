package it.unibo.osmos.redux

import it.unibo.osmos.redux.ecs.components._
import it.unibo.osmos.redux.ecs.entities.builders.CellBuilder
import it.unibo.osmos.redux.ecs.entities.{EntityManager, EntityType}
import it.unibo.osmos.redux.ecs.systems.GravitySystem
import it.unibo.osmos.redux.utils.Point
import org.scalactic.Tolerance._
import org.scalatest.{BeforeAndAfter, FunSuite}

class TestGravitySystem extends FunSuite with BeforeAndAfter {

  val TOLERANCE = 0.01
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
  val repulsePosition = PositionComponent(Point(7, 2))
  var acceleration = AccelerationComponent(0, 0)

  var gravitySystem: GravitySystem = _

  before(gravitySystem = GravitySystem())

  after {
    EntityManager.clear()
    acceleration = AccelerationComponent(0, 0)
  }

  test("check mass calculation") {
    val gravityCellEntity = CellBuilder().withDimension(dimension).withSpecificWeight(specificWeight).buildGravityEntity()
    assert(gravityCellEntity.getMassComponent.mass === 42.411 +- TOLERANCE)
  }

  test("Acceleration of CellEntity should not change without GravityCellEntity") {
    val cellEntity = CellBuilder().buildCellEntity()
    EntityManager.add(cellEntity)
    val originalAcceleration = cellEntity.getAccelerationComponent.copy()
    gravitySystem.update()
    assert(cellEntity.getAccelerationComponent.vector.x === originalAcceleration.vector.x)
    assert(cellEntity.getAccelerationComponent.vector.y === originalAcceleration.vector.y)
  }

  test("Attractive GravityCellEntity should change acceleration of CellEntity to attract") {
    val cellEntity = CellBuilder().withDimension(dimension1).withPosition(position1).buildCellEntity()
    val gravity = CellBuilder().withSpecificWeight(specificWeight)
      .withDimension(dimension).withPosition(position)
      .withEntityType(EntityType.Attractive).buildGravityEntity()
    EntityManager.add(cellEntity)
    EntityManager.add(gravity)
    gravitySystem.update()
    assert(cellEntity.getAccelerationComponent.vector.x === -1.017 +- TOLERANCE)
    assert(cellEntity.getAccelerationComponent.vector.y === -1.357 +- TOLERANCE)
  }

  test("Repulse GravityCellEntity should change acceleration of CellEntity to repulse") {
    val cellEntity = CellBuilder().withDimension(dimension1).withPosition(position1).buildCellEntity()
    val gravity = CellBuilder().withSpecificWeight(repulseSpecificWeight)
      .withDimension(repulseDimension).withPosition(repulsePosition)
      .withEntityType(EntityType.Repulsive).buildGravityEntity()
    EntityManager.add(cellEntity)
    EntityManager.add(gravity)
    gravitySystem.update()
    assert(cellEntity.getAccelerationComponent.vector.x === -4.496 +- TOLERANCE)
    assert(cellEntity.getAccelerationComponent.vector.y === 2.248 +- TOLERANCE)
  }

  test("More GravityCellEntity") {
    val cellEntity = CellBuilder().withDimension(dimension1).withPosition(position1).buildCellEntity()
    val gravityAttractive = CellBuilder().withSpecificWeight(specificWeight)
      .withDimension(dimension).withPosition(position)
      .withEntityType(EntityType.Attractive).buildGravityEntity()
    val gravityRepulse = CellBuilder().withSpecificWeight(repulseSpecificWeight)
      .withDimension(repulseDimension).withPosition(repulsePosition)
      .withEntityType(EntityType.Repulsive)
      .withAcceleration(1, 1).buildGravityEntity()
    EntityManager.add(cellEntity)
    EntityManager.add(gravityAttractive)
    EntityManager.add(gravityRepulse)
    gravitySystem.update()
    assert(cellEntity.getAccelerationComponent.vector.x === -5.513 +- TOLERANCE)
    assert(cellEntity.getAccelerationComponent.vector.y === 0.891 +- TOLERANCE)
    assert(gravityAttractive.getAccelerationComponent.vector.x === -1.824 +- TOLERANCE)
    assert(gravityAttractive.getAccelerationComponent.vector.y === -0.521 +- TOLERANCE)
    assert(gravityRepulse.getAccelerationComponent.vector.x === 0.230 +- TOLERANCE)
    assert(gravityRepulse.getAccelerationComponent.vector.y === 0.780 +- TOLERANCE)
  }
}
