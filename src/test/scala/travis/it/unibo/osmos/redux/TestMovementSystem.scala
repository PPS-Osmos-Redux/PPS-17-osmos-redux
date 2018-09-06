package it.unibo.osmos.redux

import it.unibo.osmos.redux.ecs.entities.EntityManager
import it.unibo.osmos.redux.ecs.entities.builders.CellBuilder
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

    val cellEntity = CellBuilder().withAcceleration(1,1).withSpeed(2,0).withPosition(110,170).buildCellEntity()

    val playerCellEntity = CellBuilder().withAcceleration(-4, -1).withSpeed(4, 0).withPosition(130, 150).buildPlayerEntity()

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

    val cellEntity = CellBuilder().withAcceleration(4,2).withSpeed(2,2).withPosition(110,170).buildCellEntity()

    EntityManager.add(cellEntity)

    movementSystem.update()

    assert(===(cellEntity.getSpeedComponent.vector, Vector(3.328, 2.218)))
    assert(===(cellEntity.getPositionComponent.point, Point(113.328, 172.218)))
    assert(cellEntity.getAccelerationComponent.vector === Vector(0.0, 0.0))
  }
}
