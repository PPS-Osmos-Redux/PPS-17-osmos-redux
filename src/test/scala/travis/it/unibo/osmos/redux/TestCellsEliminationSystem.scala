package it.unibo.osmos.redux

import it.unibo.osmos.redux.ecs.entities.{CellBuilder, EntityManager}
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

    val ce = CellBuilder().withDimension(system.radiusThreshold - 0.1).buildCellEntity()
    val pce = CellBuilder().withDimension(system.radiusThreshold + 0.1).buildPlayerEntity()

    assert(system.entitiesSize == 0)

    //Cell entity with radius less than threshold
    EntityManager.add(ce)
    system.update()
    assert(system.entitiesSize == 0)

    //Cell entity with radius greater than threshold
    EntityManager.add(pce)
    system.update()
    assert(system.entitiesSize == 1)

    //Update cell entity setting the radius value less than threshold
    pce.getDimensionComponent.radius_(system.radiusThreshold - 1)
    system.update()
    assert(system.entitiesSize == 0)

    EntityManager.clear()
  }
}
