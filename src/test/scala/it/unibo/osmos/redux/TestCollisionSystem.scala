package it.unibo.osmos.redux

import it.unibo.osmos.redux.ecs.components._
import it.unibo.osmos.redux.ecs.entities.{CellEntity, EntityManager, EntityType}
import it.unibo.osmos.redux.ecs.systems.CollisionSystem
import it.unibo.osmos.redux.mvc.model.MapShape.{Circle, Rectangle}
import it.unibo.osmos.redux.mvc.model._
import it.unibo.osmos.redux.utils.Point
import org.scalatest.{BeforeAndAfter, FunSuite}

class TestCollisionSystem extends FunSuite with BeforeAndAfter {

  val entity1 = CellEntity(AccelerationComponent(0, 0), CollidableComponent(true), DimensionComponent(5),
    PositionComponent(Point(70, 50)), SpeedComponent(0, 0), VisibleComponent(true), TypeComponent(EntityType.Matter))
  val entity2 = CellEntity(AccelerationComponent(0, 0), CollidableComponent(true), DimensionComponent(2),
    PositionComponent(Point(60, 80)), SpeedComponent(0, 0), VisibleComponent(true), TypeComponent(EntityType.Matter))
  val antiMatterEntity = CellEntity(AccelerationComponent(0, 0), CollidableComponent(true), DimensionComponent(2),
    PositionComponent(Point(65, 81)), SpeedComponent(0, 0), VisibleComponent(true), TypeComponent(EntityType.AntiMatter))

  var levelInfo: Level = _

  before {
    setupLevelInfo(Rectangle((100, 100), 100, 150), CollisionRules.bouncing)
  }

  after {
    EntityManager.clear()
  }

  private def setupLevelInfo(mapShape: MapShape, collisionRules: CollisionRules.Value) {
    levelInfo = Level(1,
      LevelMap(mapShape, collisionRules),
      null,
      VictoryRules.becomeTheBiggest)
  }

  test("CollisionSystem should not collide the entity with herself") {
    val system = CollisionSystem(levelInfo)

    val originalDim = entity1.getDimensionComponent
    val originalAccel = entity1.getAccelerationComponent

    EntityManager.add(entity1)

    system.update()

    assert(entity1.getDimensionComponent == originalDim && entity1.getAccelerationComponent == originalAccel)
  }

  test("CollisionSystem should not consider entities that do not have CollisionProperty") {
    val system = CollisionSystem(levelInfo)

    entity1.getPositionComponent.point_(Point(56, 79))
    entity2.getCollidableComponent.setCollidable(true)
    entity2.getPositionComponent.point_(Point(60, 80))
    entity2.getCollidableComponent.setCollidable(false)

    val originalDim1 = entity1.getDimensionComponent.radius
    val originalAccel1 = entity1.getAccelerationComponent.vector
    val originalDim2 = entity2.getDimensionComponent.radius
    val originalAccel2 = entity2.getAccelerationComponent.vector

    EntityManager.add(entity1)
    EntityManager.add(entity2)

    system.update()

    assert(entity1.getDimensionComponent.radius == originalDim1 && entity1.getAccelerationComponent.vector == originalAccel1 &&
      entity2.getDimensionComponent.radius == originalDim2 && entity2.getAccelerationComponent.vector == originalAccel2)
  }

  test("CollisionSystem should not collide two entities if the distance between the centers is greater than the sum of their radii") {
    val system = CollisionSystem(levelInfo)

    val originalDim1 = entity1.getDimensionComponent
    val originalAccel1 = entity1.getAccelerationComponent
    val originalDim2 = entity2.getDimensionComponent
    val originalAccel2 = entity2.getAccelerationComponent

    EntityManager.add(entity1)
    EntityManager.add(entity2)

    system.update()

    assert(entity1.getDimensionComponent == originalDim1 && entity1.getAccelerationComponent == originalAccel1 &&
      entity2.getDimensionComponent == originalDim2 && entity2.getAccelerationComponent == originalAccel2)
  }

  test("CollisionSystem should collide two entities if the distance between the centers is less than the sum of their radii") {
    val system = CollisionSystem(levelInfo)

    entity1.getDimensionComponent.radius_(5)
    entity1.getPositionComponent.point_(Point(60, 80))
    entity1.getCollidableComponent.setCollidable(true)
    entity2.getDimensionComponent.radius_(2)
    entity2.getPositionComponent.point_(Point(66, 80))
    entity2.getCollidableComponent.setCollidable(true)

    EntityManager.add(entity1)
    EntityManager.add(entity2)

    system.update()

    assert(entity1.getDimensionComponent.radius == 5.2)
    assert(entity1.getPositionComponent.point == Point(59.5, 80))
    assert(entity2.getDimensionComponent.radius == 1.8)
    assert(entity2.getPositionComponent.point == Point(66.5, 80))
  }

  test("Collision with AntiMatter entity should reduce both dimension's entity") {
    val system = CollisionSystem(levelInfo)

    entity1.getDimensionComponent.radius_(5)
    entity1.getPositionComponent.point_(Point(60, 80))
    entity1.getCollidableComponent.setCollidable(true)

    val originalDim1 = DimensionComponent(entity1.getDimensionComponent.radius)
    val originalAccel1 = AccelerationComponent(entity1.getAccelerationComponent.vector.x, entity1.getAccelerationComponent.vector.y)
    val originalDim2 = DimensionComponent(antiMatterEntity.getDimensionComponent.radius)
    val originalAccel2 = AccelerationComponent(antiMatterEntity.getAccelerationComponent.vector.x, antiMatterEntity.getAccelerationComponent.vector.y)

    EntityManager.add(entity1)
    EntityManager.add(antiMatterEntity)

    system.update()

    assert(entity1.getDimensionComponent.radius < originalDim1.radius && entity1.getAccelerationComponent != originalAccel1 &&
      antiMatterEntity.getDimensionComponent.radius < originalDim2.radius && antiMatterEntity.getAccelerationComponent != originalAccel2)
  }

  test("Collision with rectangular shape border, using bouncing collision rule, bounces entities back") {
    setupLevelInfo(Rectangle((160, 100), 100, 160), CollisionRules.bouncing)
    val system = CollisionSystem(levelInfo)

    val lcca = AccelerationComponent(0, 0)
    val lccc = CollidableComponent(true)
    val lccd = DimensionComponent(2)
    val lccp = PositionComponent(Point(79, 58))
    val lccs = SpeedComponent(-4, 2)
    val lccv = VisibleComponent(true)
    val lcct = TypeComponent(EntityType.Matter)
    val leftCollisionCellEntity = CellEntity(lcca, lccc, lccd, lccp, lccs, lccv, lcct)

    val rcca = AccelerationComponent(0, 0)
    val rccc = CollidableComponent(true)
    val rccd = DimensionComponent(7)
    val rccp = PositionComponent(Point(237, 90))
    val rccs = SpeedComponent(6, 0)
    val rccv = VisibleComponent(true)
    val rcct = TypeComponent(EntityType.Matter)
    val rightCollisionCellEntity = CellEntity(rcca, rccc, rccd, rccp, rccs, rccv, rcct)

    val tcca = AccelerationComponent(0, 0)
    val tccc = CollidableComponent(true)
    val tccd = DimensionComponent(8)
    val tccp = PositionComponent(Point(166, 56))
    val tccs = SpeedComponent(6, -4)
    val tccv = VisibleComponent(true)
    val tcct = TypeComponent(EntityType.Matter)
    val topCollisionCellEntity = CellEntity(tcca, tccc, tccd, tccp, tccs, tccv, tcct)

    val bcca = AccelerationComponent(0, 0)
    val bccc = CollidableComponent(true)
    val bccd = DimensionComponent(5)
    val bccp = PositionComponent(Point(113, 151))
    val bccs = SpeedComponent(-2, 7)
    val bccv = VisibleComponent(true)
    val bcct = TypeComponent(EntityType.Matter)
    val bottomCollisionCellEntity = CellEntity(bcca, bccc, bccd, bccp, bccs, bccv, bcct)

    EntityManager.add(leftCollisionCellEntity)
    EntityManager.add(rightCollisionCellEntity)
    EntityManager.add(topCollisionCellEntity)
    EntityManager.add(bottomCollisionCellEntity)

    system.update()

    assert(leftCollisionCellEntity.getSpeedComponent == SpeedComponent(4.0, 2.0))
    assert(leftCollisionCellEntity.getPositionComponent.point == Point(85.0, 58.0))

    assert(rightCollisionCellEntity.getSpeedComponent == SpeedComponent(-6.0, 0.0))
    assert(rightCollisionCellEntity.getPositionComponent.point == Point(229.0, 90.0))

    assert(topCollisionCellEntity.getSpeedComponent == SpeedComponent(6.0, 4.0))
    assert(topCollisionCellEntity.getPositionComponent.point == Point(166.0, 60.0))

    assert(bottomCollisionCellEntity.getSpeedComponent == SpeedComponent(-2.0, -7.0))
    assert(bottomCollisionCellEntity.getPositionComponent.point == Point(113.0, 139.0))
  }

  test("Collision with rectangular shape border, using instant death collision rule, reduces entities' radius") {
    setupLevelInfo(Rectangle((160, 100), 100, 160), CollisionRules.instantDeath)
    val system = CollisionSystem(levelInfo)

    val lcca = AccelerationComponent(0, 0)
    val lccc = CollidableComponent(true)
    val lccd = DimensionComponent(2)
    val lccp = PositionComponent(Point(79, 58))
    val lccs = SpeedComponent(-4, 2)
    val lccv = VisibleComponent(true)
    val lcct = TypeComponent(EntityType.Matter)
    val leftCollisionCellEntity = CellEntity(lcca, lccc, lccd, lccp, lccs, lccv, lcct)

    val rcca = AccelerationComponent(0, 0)
    val rccc = CollidableComponent(true)
    val rccd = DimensionComponent(7)
    val rccp = PositionComponent(Point(237, 90))
    val rccs = SpeedComponent(6, 0)
    val rccv = VisibleComponent(true)
    val rcct = TypeComponent(EntityType.Matter)
    val rightCollisionCellEntity = CellEntity(rcca, rccc, rccd, rccp, rccs, rccv, rcct)

    val tcca = AccelerationComponent(0, 0)
    val tccc = CollidableComponent(true)
    val tccd = DimensionComponent(8)
    val tccp = PositionComponent(Point(166, 56))
    val tccs = SpeedComponent(6, -4)
    val tccv = VisibleComponent(true)
    val tcct = TypeComponent(EntityType.Matter)
    val topCollisionCellEntity = CellEntity(tcca, tccc, tccd, tccp, tccs, tccv, tcct)

    val bcca = AccelerationComponent(0, 0)
    val bccc = CollidableComponent(true)
    val bccd = DimensionComponent(5)
    val bccp = PositionComponent(Point(113, 151))
    val bccs = SpeedComponent(-2, 7)
    val bccv = VisibleComponent(true)
    val bcct = TypeComponent(EntityType.Matter)
    val bottomCollisionCellEntity = CellEntity(bcca, bccc, bccd, bccp, bccs, bccv, bcct)

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
    val mapShape = Circle((levelCenter.x, levelCenter.y), levelRadius)
    setupLevelInfo(mapShape, CollisionRules.bouncing)
    val system = CollisionSystem(levelInfo)

    val ca = AccelerationComponent(0, 0)
    val cc = CollidableComponent(true)
    val cd = DimensionComponent(20)
    val cp = PositionComponent(Point(108, 280))
    val cs = SpeedComponent(-10.0, -20.0)
    val cv = VisibleComponent(true)
    val ct = TypeComponent(EntityType.Matter)
    val cellEntity = CellEntity(ca, cc, cd, cp, cs, cv, ct)

    EntityManager.add(cellEntity)

    system.update()

    assert(cellEntity.getPositionComponent.point == Point(120.04654311426577, 304.09308622853155))
    assert(cellEntity.getSpeedComponent.vector == utils.Vector(9.080318896799085, -20.43398660889337))
  }

  test("Collision with circular shape border, using instant death collision rule, reduces entities' radius") {
    val levelCenter = Point(300.0, 300.0)
    val levelRadius = 200.0
    val mapShape = Circle((levelCenter.x, levelCenter.y), levelRadius)
    setupLevelInfo(mapShape, CollisionRules.instantDeath)
    val system = CollisionSystem(levelInfo)

    val ca = AccelerationComponent(0, 0)
    val cc = CollidableComponent(true)
    val cd = DimensionComponent(20)
    val cp = PositionComponent(Point(108, 280))
    val cs = SpeedComponent(-10.0, -20.0)
    val cv = VisibleComponent(true)
    val ct = TypeComponent(EntityType.Matter)
    val cellEntity = CellEntity(ca, cc, cd, cp, cs, cv, ct)

    EntityManager.add(cellEntity)

    system.update()

    assert(cellEntity.getPositionComponent.point == Point(108.0, 280.0))
    assert(cellEntity.getSpeedComponent.vector == utils.Vector(-10.0, -20.0))
    assert(cellEntity.getDimensionComponent.radius == 6.961143807781525)
  }

  test("After collision between two entities, both entities remain within the map") {
    val system = CollisionSystem(levelInfo)

    entity1.getDimensionComponent.radius_(10)
    entity1.getPositionComponent.point_(Point(61, 36))
    entity1.getCollidableComponent.setCollidable(true)
    entity2.getDimensionComponent.radius_(6)
    entity2.getPositionComponent.point_(Point(60, 49))
    entity2.getCollidableComponent.setCollidable(true)

    EntityManager.add(entity1)
    EntityManager.add(entity2)

    system.update()
    val map = levelInfo.levelMap.mapShape.asInstanceOf[Rectangle]
    val boundaryLeft = map.center._1 - map.base/2
    val boundaryRight = map.center._1 + map.base/2
    val boundaryTop = map.center._2 + map.height/2
    val boundaryBottom = map.center._2 - map.height/2

    assert(entity1.getPositionComponent.point.x >= boundaryLeft + entity1.getDimensionComponent.radius)
    assert(entity1.getPositionComponent.point.x <= boundaryRight - entity1.getDimensionComponent.radius)
    assert(entity1.getPositionComponent.point.y >= boundaryBottom + entity1.getDimensionComponent.radius)
    assert(entity1.getPositionComponent.point.y <= boundaryTop - entity1.getDimensionComponent.radius)
    assert(entity2.getPositionComponent.point.x >= boundaryLeft + entity2.getDimensionComponent.radius)
    assert(entity2.getPositionComponent.point.x <= boundaryRight - entity2.getDimensionComponent.radius)
    assert(entity2.getPositionComponent.point.y >= boundaryBottom + entity2.getDimensionComponent.radius)
    assert(entity2.getPositionComponent.point.y <= boundaryTop - entity2.getDimensionComponent.radius)  }
}
