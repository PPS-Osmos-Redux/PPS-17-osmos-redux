package it.unibo.osmos.redux

import it.unibo.osmos.redux.ecs.components._
import it.unibo.osmos.redux.ecs.entities.builders.CellBuilder
import it.unibo.osmos.redux.ecs.entities.{EntityManager, EntityType}
import it.unibo.osmos.redux.ecs.systems.CollisionSystem
import it.unibo.osmos.redux.mvc.controller.levels.structure.MapShape.{Circle, Rectangle}
import it.unibo.osmos.redux.mvc.controller.levels.structure._
import it.unibo.osmos.redux.utils.Point
import org.scalactic.Tolerance._
import org.scalatest.{BeforeAndAfter, FunSuite}

class TestCollisionSystem extends FunSuite with BeforeAndAfter {

  val TOLERANCE = 0.01
  val dimension:Double = 5
  val dimension1:Double = 2

  var levelInfo: Level = _

  before {
    setupLevelInfo(Rectangle(Point(100, 100), 100, 150), CollisionRules.bouncing)
  }

  after {
    EntityManager.clear()
  }

  private def setupLevelInfo(mapShape: MapShape, collisionRules: CollisionRules.Value) {
    levelInfo = Level(LevelInfo(1.toString, VictoryRules.becomeTheBiggest),
      LevelMap(mapShape, collisionRules),
      List())
  }

  test("CollisionSystem should not collide the entity with herself") {
    val system = CollisionSystem(levelInfo)

    val entity = CellBuilder().withPosition(70,50).withDimension(dimension).buildCellEntity()

    val originalDim = entity.getDimensionComponent.copy()
    val originalAccel = entity.getAccelerationComponent.copy()

    EntityManager.add(entity)

    system.update()

    assert(entity.getDimensionComponent == originalDim && entity.getAccelerationComponent == originalAccel)
  }

  test("CollisionSystem should not consider entities that do not have CollisionProperty") {
    val system = CollisionSystem(levelInfo)

    val entity = CellBuilder().withPosition(56, 79).withDimension(dimension).buildCellEntity()
    val entity1 = CellBuilder().withPosition(60, 80).withDimension(dimension1).collidable(false).buildCellEntity()

    val originalDim = entity.getDimensionComponent.radius
    val originalAccel = entity.getAccelerationComponent.vector
    val originalDim1 = entity1.getDimensionComponent.radius
    val originalAccel1 = entity1.getAccelerationComponent.vector

    EntityManager.add(entity)
    EntityManager.add(entity1)

    system.update()

    assert(entity.getDimensionComponent.radius == originalDim && entity.getAccelerationComponent.vector == originalAccel &&
      entity1.getDimensionComponent.radius == originalDim1 && entity1.getAccelerationComponent.vector == originalAccel1)
  }

  test("CollisionSystem should not collide two entities if the distance between the centers is greater than the sum of their radii") {
    val system = CollisionSystem(levelInfo)

    val entity = CellBuilder().withPosition(70, 50).withDimension(dimension).buildCellEntity()
    val entity1 = CellBuilder().withPosition(60, 80).withDimension(dimension1).buildCellEntity()

    val originalDim = entity.getDimensionComponent
    val originalAccel = entity.getAccelerationComponent
    val originalDim1 = entity1.getDimensionComponent
    val originalAccel1 = entity1.getAccelerationComponent

    EntityManager.add(entity)
    EntityManager.add(entity1)

    system.update()

    assert(entity.getDimensionComponent == originalDim && entity.getAccelerationComponent == originalAccel &&
      entity1.getDimensionComponent == originalDim1 && entity1.getAccelerationComponent == originalAccel1)
  }

  test("CollisionSystem should collide two entities if the distance between the centers is less than the sum of their radii") {
    val system = CollisionSystem(levelInfo)

    val entity = CellBuilder().withPosition(60, 80).withDimension(dimension).buildCellEntity()
    val entity1 = CellBuilder().withPosition(66, 80).withDimension(dimension1).buildCellEntity()

    EntityManager.add(entity)
    EntityManager.add(entity1)

    system.update()

    assert(entity.getDimensionComponent.radius === 5.13 +- TOLERANCE)
    assert(entity.getPositionComponent.point.x === 59.62 +- TOLERANCE)
    assert(entity.getPositionComponent.point.y == 80)
    assert(entity1.getDimensionComponent.radius == 1.62)
    assert(entity1.getPositionComponent.point.x === 66.37 +- TOLERANCE)
    assert(entity1.getPositionComponent.point.y == 80)
  }

  test("Collision with AntiMatter entity should reduce both dimension's entity") {
    val system = CollisionSystem(levelInfo)

    val entity = CellBuilder().withPosition(60, 80).withDimension(dimension).buildCellEntity()
    val antiMatterEntity =  CellBuilder().withPosition(65, 81).withDimension(dimension1)
                                              .withEntityType(EntityType.AntiMatter).buildCellEntity()

    val originalDim = entity.getDimensionComponent.copy()
    val originalAccel =entity.getAccelerationComponent.copy()
    val originalDim1 = antiMatterEntity.getDimensionComponent.copy()
    val originalAccel1 = antiMatterEntity.getAccelerationComponent.copy()

    EntityManager.add(entity)
    EntityManager.add(antiMatterEntity)

    system.update()

    assert(entity.getDimensionComponent.radius < originalDim.radius && entity.getAccelerationComponent != originalAccel &&
      antiMatterEntity.getDimensionComponent.radius < originalDim1.radius && antiMatterEntity.getAccelerationComponent != originalAccel1)
  }

  test("Collision with rectangular shape border, using bouncing collision rule, bounces entities back") {
    setupLevelInfo(Rectangle(Point(160, 100), 100, 160), CollisionRules.bouncing)
    val system = CollisionSystem(levelInfo)

    val lccd = DimensionComponent(2)
    val lccp = PositionComponent(Point(79, 58))
    val lccs = SpeedComponent(-4, 2)
    val leftCollisionCellEntity = CellBuilder().withPosition(lccp).withDimension(lccd).withSpeed(lccs).buildCellEntity()


    val rccd = DimensionComponent(7)
    val rccp = PositionComponent(Point(237, 90))
    val rccs = SpeedComponent(6, 0)
    val rightCollisionCellEntity = CellBuilder().withPosition(rccp).withDimension(rccd).withSpeed(rccs).buildCellEntity()

    val tccd = DimensionComponent(8)
    val tccp = PositionComponent(Point(166, 56))
    val tccs = SpeedComponent(6, -4)
    val topCollisionCellEntity = CellBuilder().withPosition(tccp).withDimension(tccd).withSpeed(tccs).buildCellEntity()

    val bccd = DimensionComponent(5)
    val bccp = PositionComponent(Point(113, 151))
    val bccs = SpeedComponent(-2, 7)
    val bottomCollisionCellEntity = CellBuilder().withPosition(bccp).withDimension(bccd).withSpeed(bccs).buildCellEntity()

    EntityManager.add(leftCollisionCellEntity)
    EntityManager.add(rightCollisionCellEntity)
    EntityManager.add(topCollisionCellEntity)
    EntityManager.add(bottomCollisionCellEntity)

    system.update()

    assert(leftCollisionCellEntity.getSpeedComponent == SpeedComponent(4.0, 2.0))
    assert(leftCollisionCellEntity.getPositionComponent.point == Point(82.0, 58.0))

    assert(rightCollisionCellEntity.getSpeedComponent == SpeedComponent(-6.0, 0.0))
    assert(rightCollisionCellEntity.getPositionComponent.point == Point(233.0, 90.0))

    assert(topCollisionCellEntity.getSpeedComponent == SpeedComponent(6.0, 4.0))
    assert(topCollisionCellEntity.getPositionComponent.point == Point(166.0, 58.0))

    assert(bottomCollisionCellEntity.getSpeedComponent == SpeedComponent(-2.0, -7.0))
    assert(bottomCollisionCellEntity.getPositionComponent.point == Point(113.0, 145.0))
  }

  test("Collision with rectangular shape border, using instant death collision rule, reduces entities' radius") {
    setupLevelInfo(Rectangle(Point(160, 100), 100, 160), CollisionRules.instantDeath)
    val system = CollisionSystem(levelInfo)

    val lccd = DimensionComponent(2)
    val lccp = PositionComponent(Point(79, 58))
    val lccs = SpeedComponent(-4, 2)
    val leftCollisionCellEntity = CellBuilder().withPosition(lccp).withDimension(lccd).withSpeed(lccs).buildCellEntity()

    val rccd = DimensionComponent(7)
    val rccp = PositionComponent(Point(237, 90))
    val rccs = SpeedComponent(6, 0)
    val rightCollisionCellEntity = CellBuilder().withPosition(rccp).withDimension(rccd).withSpeed(rccs).buildCellEntity()

    val tccd = DimensionComponent(8)
    val tccp = PositionComponent(Point(166, 56))
    val tccs = SpeedComponent(6, -4)
    val topCollisionCellEntity = CellBuilder().withPosition(tccp).withDimension(tccd).withSpeed(tccs).buildCellEntity()

    val bccd = DimensionComponent(5)
    val bccp = PositionComponent(Point(113, 151))
    val bccs = SpeedComponent(-2, 7)
    val bottomCollisionCellEntity = CellBuilder().withPosition(bccp).withDimension(bccd).withSpeed(bccs).buildCellEntity()

    EntityManager.add(leftCollisionCellEntity)
    EntityManager.add(rightCollisionCellEntity)
    EntityManager.add(topCollisionCellEntity)
    EntityManager.add(bottomCollisionCellEntity)

    system.update()

    assert(leftCollisionCellEntity.getSpeedComponent == lccs)
    assert(leftCollisionCellEntity.getPositionComponent.point == Point(79.0, 58.0))
    assert(leftCollisionCellEntity.getDimensionComponent.radius == -1)

    assert(rightCollisionCellEntity.getSpeedComponent == rccs)
    assert(rightCollisionCellEntity.getPositionComponent.point == Point(237.0, 90.0))
    assert(rightCollisionCellEntity.getDimensionComponent.radius == 3.0)

    assert(topCollisionCellEntity.getSpeedComponent == tccs)
    assert(topCollisionCellEntity.getPositionComponent.point == Point(166.0, 56.0))
    assert(topCollisionCellEntity.getDimensionComponent.radius == 6.0)

    assert(bottomCollisionCellEntity.getSpeedComponent == bccs)
    assert(bottomCollisionCellEntity.getPositionComponent.point == Point(113.0, 151.0))
    assert(bottomCollisionCellEntity.getDimensionComponent.radius == -1)
  }

  test("Collision with circular shape border, using bouncing collision rule, bounces entities back") {
    val levelCenter = Point(300.0, 300.0)
    val levelRadius = 200.0
    val mapShape = Circle(Point(levelCenter.x, levelCenter.y), levelRadius)
    setupLevelInfo(mapShape, CollisionRules.bouncing)
    val system = CollisionSystem(levelInfo)

    val cd = DimensionComponent(20)
    val cp = PositionComponent(Point(108, 280))
    val cs = SpeedComponent(-10.0, -20.0)
    val cellEntity = CellBuilder().withPosition(cp).withDimension(cd).withSpeed(cs).buildCellEntity()

    EntityManager.add(cellEntity)

    system.update()

    assert(cellEntity.getPositionComponent.point == Point(120.04654311426577, 304.09308622853155))
    assert(cellEntity.getSpeedComponent.vector == utils.Vector(9.080318896799085, -20.43398660889337))
  }

  test("Collision with circular shape border, using instant death collision rule, reduces entities' radius") {
    val levelCenter = Point(300.0, 300.0)
    val levelRadius = 200.0
    val mapShape = Circle(Point(levelCenter.x, levelCenter.y), levelRadius)
    setupLevelInfo(mapShape, CollisionRules.instantDeath)
    val system = CollisionSystem(levelInfo)

    val cd = DimensionComponent(20)
    val cp = PositionComponent(Point(108, 280))
    val cs = SpeedComponent(-10.0, -20.0)
    val cellEntity = CellBuilder().withPosition(cp).withDimension(cd).withSpeed(cs).buildCellEntity()

    EntityManager.add(cellEntity)

    system.update()

    assert(cellEntity.getPositionComponent.point == Point(108.0, 280.0))
    assert(cellEntity.getSpeedComponent.vector == utils.Vector(-10.0, -20.0))
    assert(cellEntity.getDimensionComponent.radius == 6.961143807781525)
  }

  test("After collision between two entities, both entities remain within the map") {
    val system = CollisionSystem(levelInfo)

    val entity = CellBuilder().withPosition(61, 36).withDimension(10).buildCellEntity()
    val entity1 = CellBuilder().withPosition(60, 49).withDimension(6).buildCellEntity()

    EntityManager.add(entity)
    EntityManager.add(entity1)

    system.update()
    val map = levelInfo.levelMap.mapShape.asInstanceOf[Rectangle]
    val boundaryLeft = map.center.x - map.base / 2
    val boundaryRight = map.center.x + map.base / 2
    val boundaryTop = map.center.y + map.height / 2
    val boundaryBottom = map.center.y - map.height / 2

    assert(entity.getPositionComponent.point.x >= boundaryLeft + entity.getDimensionComponent.radius)
    assert(entity.getPositionComponent.point.x <= boundaryRight - entity.getDimensionComponent.radius)
    assert(entity.getPositionComponent.point.y >= boundaryBottom + entity.getDimensionComponent.radius)
    assert(entity.getPositionComponent.point.y <= boundaryTop - entity.getDimensionComponent.radius)
    assert(entity1.getPositionComponent.point.x >= boundaryLeft + entity1.getDimensionComponent.radius)
    assert(entity1.getPositionComponent.point.x <= boundaryRight - entity1.getDimensionComponent.radius)
    assert(entity1.getPositionComponent.point.y >= boundaryBottom + entity1.getDimensionComponent.radius)
    assert(entity1.getPositionComponent.point.y <= boundaryTop - entity1.getDimensionComponent.radius)
  }
}
