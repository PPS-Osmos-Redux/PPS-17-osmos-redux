package it.unibo.osmos.redux

import it.unibo.osmos.redux.ecs.components._
import it.unibo.osmos.redux.ecs.entities.{CellEntity, EntityManager, EntityType, PlayerCellEntity}
import it.unibo.osmos.redux.ecs.systems.MovementSystem
import it.unibo.osmos.redux.utils.{Point, Vector}
import org.scalactic.Tolerance._
import org.scalatest.{BeforeAndAfter, FunSuite}

class TestMovementSystem extends FunSuite with BeforeAndAfter {

  var movementSystem: MovementSystem = _

  before(movementSystem = MovementSystem())

  after {
    EntityManager.clear()
  }

  val TOLERANCE = 0.01

  implicit def toPair(point: Point): (Double, Double) = (point.x, point.y)

  implicit def toPair(vector: Vector): (Double, Double) = (vector.x, vector.y)

  def ===(actual: (Double, Double), expected: (Double, Double)): Boolean = {
    actual._1 === expected._1 +- TOLERANCE && actual._2 === expected._2 +- TOLERANCE
  }

  test("MovableProperty entities' acceleration, speed and position are updated correctly") {

    val ca = AccelerationComponent(1, 1)
    val cc = CollidableComponent(true)
    val cd = DimensionComponent(5)
    val cp = PositionComponent(Point(110, 170))
    val cs = SpeedComponent(2, 0)
    val cv = VisibleComponent(true)
    val ct = TypeComponent(EntityType.Matter)
    val cellEntity = CellEntity(ca, cc, cd, cp, cs, cv, ct)

    val pca = AccelerationComponent(-4, -1)
    val pcc = CollidableComponent(true)
    val pcd = DimensionComponent(5)
    val pcp = PositionComponent(Point(130, 150))
    val pcs = SpeedComponent(4, 0)
    val pcv = VisibleComponent(true)
    val pct = TypeComponent(EntityType.Controlled)
    val spw = SpawnerComponent(false)
    val playerCellEntity = PlayerCellEntity(pca, pcc, pcd, pcp, pcs, pcv, spw, pct)

    EntityManager.add(cellEntity)
    EntityManager.add(playerCellEntity)

    movementSystem.update()

    assert(cellEntity.getSpeedComponent.vector == Vector(3.0, 1.0))
    assert(cellEntity.getPositionComponent.point == Point(113.0, 171.0))
    assert(cellEntity.getAccelerationComponent.vector == Vector(0.0, 0.0))

    assert(playerCellEntity.getSpeedComponent.vector == Vector(0.0, -1.0))
    assert(playerCellEntity.getPositionComponent.point == Point(130.0, 149.0))
    assert(playerCellEntity.getAccelerationComponent.vector == Vector(0.0, 0.0))
  }

  test("MovableProperty entities' speed does not exceed max speed") {

    val ca = AccelerationComponent(4, 2)
    val cc = CollidableComponent(true)
    val cd = DimensionComponent(5)
    val cp = PositionComponent(Point(110, 170))
    val cs = SpeedComponent(2, 2)
    val cv = VisibleComponent(true)
    val ct = TypeComponent(EntityType.Matter)
    val cellEntity = CellEntity(ca, cc, cd, cp, cs, cv, ct)

    EntityManager.add(cellEntity)

    movementSystem.update()

    assert(===(cellEntity.getSpeedComponent.vector, Vector(3.328, 2.218)))
    assert(===(cellEntity.getPositionComponent.point, Point(113.328, 172.218)))
    assert(cellEntity.getAccelerationComponent.vector === Vector(0.0, 0.0))
  }
}
