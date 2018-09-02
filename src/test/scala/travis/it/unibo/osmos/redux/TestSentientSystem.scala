package travis.it.unibo.osmos.redux

import it.unibo.osmos.redux.ecs.components._
import it.unibo.osmos.redux.ecs.entities.builders.{CellBuilder, SentientCellBuilder}
import it.unibo.osmos.redux.ecs.entities.{CellEntity, EntityManager, EntityType, SentientCellEntity}
import it.unibo.osmos.redux.ecs.systems.sentient.SentientSystem
import it.unibo.osmos.redux.mvc.model._
import it.unibo.osmos.redux.mvc.model.MapShape.Rectangle
import it.unibo.osmos.redux.utils._
import org.scalatest.{BeforeAndAfter, FunSuite}
import org.scalactic.Tolerance._

class TestSentientSystem extends FunSuite with BeforeAndAfter{

  val TOLERANCE = 0.01
  var acceleration = AccelerationComponent(0, 0)
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

  var levelInfo: Level = _

  before {
    //rectangle with vertices (0,0) and (200,300)
    setupLevelInfo(Rectangle((100, 150), 300, 200), CollisionRules.instantDeath)
  }

  after{
    EntityManager.clear()
    acceleration = AccelerationComponent(0, 0)
  }

  private def setupLevelInfo(mapShape: MapShape, collisionRules: CollisionRules.Value) {
    levelInfo = Level(1.toString,
      LevelMap(mapShape, collisionRules),
      null,
      VictoryRules.becomeTheBiggest)
  }


  test("Acceleration of SentientCellEntity should not change without any target") {
    val sentientCellEntity = SentientCellEntity(acceleration,collidable,dimension,position,speed,visible, spawner)
    val sentientSystem = SentientSystem(levelInfo)
    EntityManager.add(sentientCellEntity)
    val originalAcceleration = AccelerationComponent(sentientCellEntity.getAccelerationComponent.vector)
    sentientSystem.update()
    assert(sentientCellEntity.getAccelerationComponent.vector.x === originalAcceleration.vector.x)
    assert(sentientCellEntity.getAccelerationComponent.vector.y === originalAcceleration.vector.y)
  }

  test("Acceleration of SentientCellEntity should change with a target in target's direction") {
    val cellEntity = CellEntity(acceleration,collidable,dimension1,position1,speed1,visible,baseTypeEntity)
    val sentientCellEntity = SentientCellEntity(acceleration,collidable,dimension,position,speed,visible, spawner)
    val system = SentientSystem(levelInfo)
    EntityManager.add(cellEntity)
    EntityManager.add(sentientCellEntity)
    system.update()
    assert(sentientCellEntity.getAccelerationComponent.vector.x === 0.1 +- TOLERANCE)
    assert(sentientCellEntity.getAccelerationComponent.vector.y === 0.0 +- TOLERANCE)
  }

  test("Acceleration of SentientCellEntity should choose the correct target and change its acceleration accordingly") {
    val cellEntity = new CellBuilder().withPosition(79,79).withDimension(4).build
    val cellEntity1 = new CellBuilder().withPosition(83,91).withDimension(2).build
    val speed = Vector(-2,3) limit Constants.Sentient.MAX_SPEED
    val sentientCellEntity = SentientCellBuilder().withPosition(89,82).withDimension(5).withSpeed(speed.x, speed.y).build
    val system = SentientSystem(levelInfo)
    EntityManager.add(cellEntity)
    EntityManager.add(cellEntity1)
    EntityManager.add(sentientCellEntity)
    system.update()
    assert(sentientCellEntity.getAccelerationComponent.vector.x === -0.033 +- TOLERANCE)
    assert(sentientCellEntity.getAccelerationComponent.vector.y === -0.09 +- TOLERANCE)
  }

  test("Acceleration of SentientCellEntity should change to avoid enemies") {
    val cellEntity = CellEntity(acceleration,collidable,dimension2,position1,speed1,visible,baseTypeEntity)
    val cellEntity1 = CellEntity(acceleration,collidable,dimension2,position2,speed1,visible,baseTypeEntity)
    val sentientCellEntity = SentientCellEntity(acceleration,collidable,dimension,position,speed,visible, spawner)
    val system = SentientSystem(levelInfo)
    EntityManager.add(cellEntity)
    EntityManager.add(cellEntity1)
    EntityManager.add(sentientCellEntity)
    system.update()
    assert(sentientCellEntity.getAccelerationComponent.vector.x === -0.094 +- TOLERANCE)
    assert(sentientCellEntity.getAccelerationComponent.vector.y === -0.011 +- TOLERANCE)
  }

  test("If collision rule with boundary is bouncing, SentientCellEntity should not change it's acceleration to avoid boundary") {
    setupLevelInfo(Rectangle((50, 100), 200, 100), CollisionRules.bouncing)
    val acceleration = AccelerationComponent(0,0)
    val sentientCellEntity = SentientCellBuilder().withPosition(24,14).withDimension(5).withSpeed(-2,3).withAcceleration(acceleration).build
    val system = SentientSystem(levelInfo)
    EntityManager.add(sentientCellEntity)
    system.update()
    assert(sentientCellEntity.getAccelerationComponent.vector == acceleration.vector)
  }

  test("If collision rule with boundary is instantDeath, SentientCellEntity should change it's acceleration to avoid boundary") {
    val acceleration = AccelerationComponent(0,0)
    val speed = Vector(-2,-3) limit Constants.Sentient.MAX_SPEED
    val sentientCellEntity = SentientCellBuilder().withPosition(84,74).withDimension(5).withSpeed(speed.x, speed.y).withAcceleration(acceleration).build
    val system = SentientSystem(levelInfo)
    EntityManager.add(sentientCellEntity)
    system.update()
    assert(sentientCellEntity.getAccelerationComponent.vector.x == acceleration.vector.x)
    assert(sentientCellEntity.getAccelerationComponent.vector.y == Constants.Sentient.MAX_ACCELERATION)
  }

  test("If a SentientCellEntity have a acceleration, the radius is decreased") {
    val cellEntity = CellEntity(acceleration,collidable,dimension2,position1,speed1,visible,baseTypeEntity)
    val cellEntity1 = CellEntity(acceleration,collidable,dimension2,position2,speed1,visible,baseTypeEntity)
    val sentientCellEntity = SentientCellEntity(acceleration,collidable,dimension,position,speed,visible, spawner)
    val system = SentientSystem(levelInfo)
    EntityManager.add(cellEntity)
    EntityManager.add(cellEntity1)
    EntityManager.add(sentientCellEntity)
    val originalAcceleration = acceleration.copy()
    val originalRadius = dimension.radius
    system.update()
    assert(!(sentientCellEntity.getAccelerationComponent.vector == originalAcceleration.vector))
    assert(sentientCellEntity.getDimensionComponent.radius < originalRadius)
  }
}
