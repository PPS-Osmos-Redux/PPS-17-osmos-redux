package it.unibo.osmos.redux

import it.unibo.osmos.redux.ecs.components._
import it.unibo.osmos.redux.ecs.entities.builders.CellBuilder
import it.unibo.osmos.redux.ecs.entities.{EntityManager, EntityType}
import it.unibo.osmos.redux.ecs.systems.SentientSystem
import it.unibo.osmos.redux.mvc.controller.levels.structure.MapShape.Rectangle
import it.unibo.osmos.redux.mvc.controller.levels.structure._
import it.unibo.osmos.redux.utils._
import org.scalactic.Tolerance._
import org.scalatest.{BeforeAndAfter, FunSuite}

class TestSentientSystem extends FunSuite with BeforeAndAfter {

  val TOLERANCE = 0.01
  val collidable = CollidableComponent(true)
  val speed = SpeedComponent(0, 0)
  val dimension = DimensionComponent(8)
  val position = PositionComponent(Point(8, 12))
  val visible = VisibleComponent(true)
  val baseTypeEntity = TypeComponent(EntityType.Matter)
  val sentientTypeEntity = TypeComponent(EntityType.Sentient)
  val dimension1 = DimensionComponent(5)
  val position1 = PositionComponent(Point(26, 12))
  val speed1 = SpeedComponent(4, 0)
  val dimension2 = DimensionComponent(10)
  val position2 = PositionComponent(Point(25, 25))
  val spawner = SpawnerComponent(false)
  var acceleration = AccelerationComponent(0, 0)
  var levelInfo: Level = _

  before {
    //rectangle with vertices (0,0) and (200,300)
    setupLevelInfo(Rectangle(Point(100, 150), 300, 200), CollisionRules.instantDeath)
  }

  after {
    EntityManager.clear()
    acceleration = AccelerationComponent(0, 0)
  }

  private def setupLevelInfo(mapShape: MapShape, collisionRules: CollisionRules.Value) {
    levelInfo = Level(LevelInfo("1", VictoryRules.becomeTheBiggest),
      LevelMap(mapShape, collisionRules),
      null)
  }


  test("Acceleration of SentientCellEntity should not change without any target") {
    val sentientCellEntity = CellBuilder().withDimension(dimension).withPosition(position).withSpeed(speed).buildSentientEntity()
      //SentientCellEntity(acceleration, collidable, dimension, position, speed, visible, spawner)
    val sentientSystem = SentientSystem(levelInfo)
    EntityManager.add(sentientCellEntity)
    val originalAcceleration = sentientCellEntity.getAccelerationComponent.copy()
    sentientSystem.update()
    assert(sentientCellEntity.getAccelerationComponent.vector.x === originalAcceleration.vector.x)
    assert(sentientCellEntity.getAccelerationComponent.vector.y === originalAcceleration.vector.y)
  }

  test("Acceleration of SentientCellEntity should change with a target in target's direction") {
    val cellEntity = CellBuilder().withDimension(dimension1).withPosition(position1)
      .withSpeed(speed1).buildCellEntity()
    val sentientCellEntity = CellBuilder().withDimension(dimension).withPosition(position)
      .withSpeed(speed).buildSentientEntity()
    val system = SentientSystem(levelInfo)
    EntityManager.add(cellEntity)
    EntityManager.add(sentientCellEntity)
    system.update()
    assert(sentientCellEntity.getAccelerationComponent.vector.x === 0.1 +- TOLERANCE)
    assert(sentientCellEntity.getAccelerationComponent.vector.y === 0.0 +- TOLERANCE)
  }

  test("Acceleration of SentientCellEntity should choose the correct target and change its acceleration accordingly") {
    val cellEntity = CellBuilder().withPosition(79, 79).withDimension(4).buildCellEntity()
    val cellEntity1 = CellBuilder().withPosition(83, 91).withDimension(2).buildCellEntity()
    val speed = Vector(-2, 3) limit Constants.Sentient.MAX_SPEED
    val sentientCellEntity = CellBuilder().withPosition(89, 82).withDimension(5)
      .withSpeed(speed).buildSentientEntity()
    val system = SentientSystem(levelInfo)
    EntityManager.add(cellEntity)
    EntityManager.add(cellEntity1)
    EntityManager.add(sentientCellEntity)
    system.update()
    assert(sentientCellEntity.getAccelerationComponent.vector.x === -0.033 +- TOLERANCE)
    assert(sentientCellEntity.getAccelerationComponent.vector.y === -0.09 +- TOLERANCE)
  }

  test("Acceleration of SentientCellEntity should change to avoid enemies") {
    val cellEntity = CellBuilder().withDimension(dimension2).withPosition(position1)
      .withSpeed(speed1).buildCellEntity()
    val cellEntity1 =  CellBuilder().withDimension(dimension2).withPosition(position2)
      .withSpeed(speed1).buildCellEntity()
    val sentientCellEntity = CellBuilder().withDimension(dimension).withPosition(position)
      .withSpeed(speed).buildSentientEntity()
    val system = SentientSystem(levelInfo)
    EntityManager.add(cellEntity)
    EntityManager.add(cellEntity1)
    EntityManager.add(sentientCellEntity)
    system.update()
    assert(sentientCellEntity.getAccelerationComponent.vector.x === -0.094 +- TOLERANCE)
    assert(sentientCellEntity.getAccelerationComponent.vector.y === -0.011 +- TOLERANCE)
  }

  test("If collision rule with boundary is bouncing, SentientCellEntity should not change it's acceleration to avoid boundary") {
    setupLevelInfo(Rectangle(Point(100, 150), 300, 200), CollisionRules.bouncing)
    val sentientCellEntity = CellBuilder().withPosition(24, 14).withDimension(5)
      .withSpeed(-2, 3).buildSentientEntity()
    val originalAcceleration = sentientCellEntity.getAccelerationComponent.copy()
    val system = SentientSystem(levelInfo)
    EntityManager.add(sentientCellEntity)
    system.update()
    assert(sentientCellEntity.getAccelerationComponent.vector == originalAcceleration.vector)
  }

  test("If collision rule with boundary is instantDeath, SentientCellEntity should change it's acceleration to avoid boundary") {
    val speed = Vector(-2, -3) limit Constants.Sentient.MAX_SPEED
    val sentientCellEntity = CellBuilder().withPosition(84, 74).withDimension(5)
      .withSpeed(speed).buildSentientEntity()
    val originalAcceleration = sentientCellEntity.getAccelerationComponent.copy()
    val system = SentientSystem(levelInfo)
    EntityManager.add(sentientCellEntity)
    system.update()
    assert(sentientCellEntity.getAccelerationComponent.vector.x == originalAcceleration.vector.x)
    assert(sentientCellEntity.getAccelerationComponent.vector.y == Constants.Sentient.MAX_ACCELERATION)
  }

  test("If a SentientCellEntity have a acceleration and a radius is less than the min to lose radius, the radius is not decreased") {
    val cellEntity =  CellBuilder().withDimension(dimension2).withPosition(position1)
      .withSpeed(speed1).buildCellEntity()
    val cellEntity1 =  CellBuilder().withDimension(dimension2).withPosition(position2)
      .withSpeed(speed1).buildCellEntity()
    val sentientCellEntity = CellBuilder().withPosition(position).withSpeed(speed)
      .withDimension(DimensionComponent(Constants.Sentient.MIN_RADIUS_FOR_LOST_RADIUS_BEHAVIOUR - 1))
      .buildSentientEntity()
    val system = SentientSystem(levelInfo)
    EntityManager.add(cellEntity)
    EntityManager.add(cellEntity1)
    EntityManager.add(sentientCellEntity)
    val originalAcceleration = sentientCellEntity.getAccelerationComponent.copy()
    val originalRadius = sentientCellEntity.getDimensionComponent.radius
    system.update()
    assert(!(sentientCellEntity.getAccelerationComponent.vector == originalAcceleration.vector))
    assert(sentientCellEntity.getDimensionComponent.radius == originalRadius)
  }

  test("If a SentientCellEntity have a acceleration and a radius is greater than the min to lose radius, the radius is decreased") {
    setupLevelInfo(Rectangle(Point(100, 150), 300, 200), CollisionRules.bouncing)
    val cellEntity =  CellBuilder().withDimension(dimension2).withPosition(17, 100).buildCellEntity()
    val cellEntity1 = CellBuilder().withDimension(dimension2).withPosition(40, 40).buildCellEntity()
    val sentientCellEntity = CellBuilder().withPosition(17, 21).withSpeed(0, 1)
      .withDimension(DimensionComponent(Constants.Sentient.MIN_RADIUS_FOR_LOST_RADIUS_BEHAVIOUR + 1))
      .buildSentientEntity()
    val system = SentientSystem(levelInfo)
    EntityManager.add(cellEntity)
    EntityManager.add(cellEntity1)
    EntityManager.add(sentientCellEntity)
    val originalAcceleration = sentientCellEntity.getAccelerationComponent.copy()
    val originalRadius = sentientCellEntity.getDimensionComponent.radius
    system.update()
    assert(sentientCellEntity.getAccelerationComponent.vector.x == originalAcceleration.vector.x)
    assert(sentientCellEntity.getAccelerationComponent.vector.y == Constants.Sentient.MAX_ACCELERATION)
    assert(sentientCellEntity.getDimensionComponent.radius < originalRadius)
  }
}
