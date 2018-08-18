package it.unibo.osmos.redux

import it.unibo.osmos.redux.ecs.components._
import it.unibo.osmos.redux.ecs.entities.{CellEntity, EntityManager, PlayerCellEntity}
import it.unibo.osmos.redux.ecs.systems.MovementSystem
import it.unibo.osmos.redux.mvc.model.MapShape.Rectangle
import it.unibo.osmos.redux.mvc.model._
import it.unibo.osmos.redux.utils.Point
import org.scalatest.{BeforeAndAfter, FunSuite}

class TestMovementSystem extends FunSuite with BeforeAndAfter {

  var movementSystem: MovementSystem = _

  after {
    EntityManager.clear()
  }

  private def initEntityManager(mapShape: MapShape, collisionRules: CollisionRules.Value){
    val levelInfo = Level(1,
      LevelMap(mapShape, collisionRules),
      null,
      VictoryRules.becomeTheBiggest,
      false)
    movementSystem = MovementSystem(levelInfo)
    EntityManager.subscribe(movementSystem, null)
  }

  test("Test speed and position update") {
    val mapShape = Rectangle((100, 150), 100, 150)
    initEntityManager(mapShape, CollisionRules.bouncing)

    val ca = AccelerationComponent(1, 1)
    val cc = CollidableComponent(true)
    val cd = DimensionComponent(5)
    val cp = PositionComponent(Point(10, 20))
    val cs = SpeedComponent(4, 0)
    val cv = VisibleComponent(true)
    val ct = TypeComponent(EntityType.Material)
    val cellEntity = CellEntity(ca, cc, cd, cp, cs, cv, ct)

    val pca = AccelerationComponent(-4, -1)
    val pcc = CollidableComponent(true)
    val pcd = DimensionComponent(5)
    val pcp = PositionComponent(Point(20, 50))
    val pcs = SpeedComponent(4, 0)
    val pcv = VisibleComponent(true)
    val pct = TypeComponent(EntityType.Material)
    val spw = SpawnerComponent(false)
    val playerCellEntity = PlayerCellEntity(pca, pcc, pcd, pcp, pcs, pcv, pct, spw)

    EntityManager.add(cellEntity)
    EntityManager.add(playerCellEntity)

    movementSystem.update()

    assert(cellEntity.getSpeedComponent == SpeedComponent(5.0, 1.0))
    assert(cellEntity.getPositionComponent.point == Point(15.0, 21.0))
    assert(cellEntity.getAccelerationComponent == AccelerationComponent(0.0, 0.0))

    assert(playerCellEntity.getSpeedComponent == SpeedComponent(0.0, -1.0))
    assert(playerCellEntity.getPositionComponent.point == Point(20.0, 49.0))
    assert(playerCellEntity.getAccelerationComponent == AccelerationComponent(0.0, 0.0))
  }

  test("Test rectangular shape field bouncing") {
    val mapShape = Rectangle((160, 100), 100, 160)
    initEntityManager(mapShape, CollisionRules.bouncing)

    /*println(mapShape.base)
    println(mapShape.height)
    println(mapShape.center._1)
    println(mapShape.center._2)*/
    val lcca = AccelerationComponent(0, 0)
    val lccc = CollidableComponent(true)
    val lccd = DimensionComponent(2)
    val lccp = PositionComponent(Point(83, 56))
    val lccs = SpeedComponent(-4, 2)
    val lccv = VisibleComponent(true)
    val lcct = TypeComponent(EntityType.Material)
    val leftCollisionCellEntity = CellEntity(lcca, lccc, lccd, lccp, lccs, lccv, lcct)

    val rcca = AccelerationComponent(0, 0)
    val rccc = CollidableComponent(true)
    val rccd = DimensionComponent(7)
    val rccp = PositionComponent(Point(231, 90))
    val rccs = SpeedComponent(6, 0)
    val rccv = VisibleComponent(true)
    val rcct = TypeComponent(EntityType.Material)
    val rightCollisionCellEntity = CellEntity(rcca, rccc, rccd, rccp, rccs, rccv, rcct)

    val tcca = AccelerationComponent(0, 0)
    val tccc = CollidableComponent(true)
    val tccd = DimensionComponent(8)
    val tccp = PositionComponent(Point(160, 60))
    val tccs = SpeedComponent(6, -4)
    val tccv = VisibleComponent(true)
    val tcct = TypeComponent(EntityType.Material)
    val topCollisionCellEntity = CellEntity(tcca, tccc, tccd, tccp, tccs, tccv, tcct)

    val bcca = AccelerationComponent(0, 0)
    val bccc = CollidableComponent(true)
    val bccd = DimensionComponent(5)
    val bccp = PositionComponent(Point(115, 144))
    val bccs = SpeedComponent(-2, 7)
    val bccv = VisibleComponent(true)
    val bcct = TypeComponent(EntityType.Material)
    val bottomCollisionCellEntity = CellEntity(bcca, bccc, bccd, bccp, bccs, bccv, bcct)

    EntityManager.add(leftCollisionCellEntity)
    EntityManager.add(rightCollisionCellEntity)
    EntityManager.add(topCollisionCellEntity)
    EntityManager.add(bottomCollisionCellEntity)

    movementSystem.update()

   assert(leftCollisionCellEntity.getSpeedComponent == SpeedComponent(4.0, 2.0))
   assert(leftCollisionCellEntity.getPositionComponent.point == Point(85.0, 58.0))

    assert(rightCollisionCellEntity.getSpeedComponent == SpeedComponent(-6.0, 0.0))
    assert(rightCollisionCellEntity.getPositionComponent.point == Point(229.0, 90.0))

    assert(topCollisionCellEntity.getSpeedComponent == SpeedComponent(6.0, 4.0))
    assert(topCollisionCellEntity.getPositionComponent.point == Point(166.0, 60.0))

    assert(bottomCollisionCellEntity.getSpeedComponent == SpeedComponent(-2.0, -7.0))
    assert(bottomCollisionCellEntity.getPositionComponent.point == Point(113.0, 139.0))
  }

  test("Test circular shape field bouncing") {
    // TODO
    /*
    val ca = AccelerationComponent(1, 1)
    val cc = CollidableComponent(true)
    val cd = DimensionComponent(5)
    val cp = PositionComponent(Point(0, 0))
    val cs = SpeedComponent(4, 0)
    val cv = VisibleComponent(true)
    val ct = TypeComponent(EntityType.Material)
    val cellEntity = CellEntity(ca, cc, cd, cp, cs, cv, ct)

    val pca = AccelerationComponent(-4, -1)
    val pcc = CollidableComponent(true)
    val pcd = DimensionComponent(5)
    val pcp = PositionComponent(Point(-4, 6))
    val pcs = SpeedComponent(4, 0)
    val pcv = VisibleComponent(true)
    val pct = TypeComponent(EntityType.Material)
    val spw = SpawnerComponent(false)
    val playerCellEntity = PlayerCellEntity(pca, pcc, pcd, pcp, pcs, pcv, pct, spw)

    EntityManager.add(cellEntity)
    EntityManager.add(playerCellEntity)

    movementSystem.update()*/
  }
}
