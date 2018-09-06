package it.unibo.osmos.redux

import it.unibo.osmos.redux.ecs.entities.EntityManager
import it.unibo.osmos.redux.ecs.entities.builders.{CellBuilder, PlayerCellBuilder}
import it.unibo.osmos.redux.ecs.systems.CellsEliminationSystem
import org.scalatest.FunSuite

/**
  * Spy class to capture entities number
  */
class CellEliminationSystemSpy() extends CellsEliminationSystem {
  def entitiesSize: Int = entities.size
}

class TestCellsEliminationSystem extends FunSuite {

  test("Cell elimination") {
    val system = new CellEliminationSystemSpy()
    val ce = new CellBuilder().withDimension(1).build
    val pce = PlayerCellBuilder().withDimension(1).build
    pce.getDimensionComponent.radius_(system.radiusThreshold + 1)
    ce.getDimensionComponent.radius_(system.radiusThreshold - 1)
    assert(system.entitiesSize == 0)
    EntityManager.add(ce)
    system.update()
    assert(system.entitiesSize == 0)
    EntityManager.add(pce)
    system.update()
    assert(system.entitiesSize == 1)
    pce.getDimensionComponent.radius_(system.radiusThreshold - 1)
    system.update()
    assert(system.entitiesSize == 0)
  }
}
