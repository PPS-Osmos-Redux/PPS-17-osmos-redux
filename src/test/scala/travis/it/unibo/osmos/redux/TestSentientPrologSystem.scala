package it.unibo.osmos.redux

import it.unibo.osmos.redux.ecs.components._
import it.unibo.osmos.redux.ecs.entities._
import it.unibo.osmos.redux.ecs.entities.builders.CellBuilder
import it.unibo.osmos.redux.ecs.systems.SentientPrologSystem
import it.unibo.osmos.redux.utils.Point
import org.scalatest.{BeforeAndAfter, FunSuite}

class TestSentientPrologSystem extends FunSuite with BeforeAndAfter {

  after {
    EntityManager.clear()
  }

  test("Sentient cell hunting smaller cells") {
    val sentientPrologSystem = SentientPrologSystem()

    val scd = DimensionComponent(10)
    val scp = PositionComponent(Point(50, 64))
    val sentientCellEntity = CellBuilder().withPosition(scp).withDimension(scd).buildSentientEntity()

    val scd2 = DimensionComponent(8)
    val scp2 = PositionComponent(Point(101, 87))
    val sentientCellEntity2 =  CellBuilder().withPosition(scp2).withDimension(scd2).buildSentientEntity()

    val ca1 = AccelerationComponent(1, 1)
    val cd1 = DimensionComponent(4)
    val cp1 = PositionComponent(Point(100, 170))
    val cs1 = SpeedComponent(4, 5)
    val cellEntity1 = CellBuilder().withAcceleration(ca1).withDimension(cd1).withPosition(cp1).withSpeed(cs1).buildCellEntity()

    val ca2 = AccelerationComponent(1, 1)
    val cd2 = DimensionComponent(8)
    val cp2 = PositionComponent(Point(30, 100))
    val cellEntity2 = CellBuilder().withAcceleration(ca2).withDimension(cd2).withPosition(cp2).buildCellEntity()

    val ca3 = AccelerationComponent(1, 1)
    val cd3 = DimensionComponent(7)
    val cp3 = PositionComponent(Point(130, 40))
    val cs3 = SpeedComponent(4, 3)
    val cellEntity3 = CellBuilder().withAcceleration(ca3).withDimension(cd3).withPosition(cp3).withSpeed(cs3).buildCellEntity()

    EntityManager.add(sentientCellEntity)
    EntityManager.add(sentientCellEntity2)
    EntityManager.add(cellEntity1)
    EntityManager.add(cellEntity2)
    EntityManager.add(cellEntity3)


    sentientPrologSystem.update()
  }

  /*test("Sentient cell running from bigger cells") {
    // TODO
    val sentientPrologSystem = SentientPrologSystem()
    EntityManager.subscribe(sentientPrologSystem, null)

    sentientPrologSystem.update()
  }*/
}
