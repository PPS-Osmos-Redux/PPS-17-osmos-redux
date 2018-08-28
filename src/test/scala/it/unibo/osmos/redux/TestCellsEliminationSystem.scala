package it.unibo.osmos.redux

import it.unibo.osmos.redux.ecs.components._
import it.unibo.osmos.redux.ecs.entities._
import it.unibo.osmos.redux.ecs.systems.CellsEliminationSystem
import org.scalatest.FunSuite

/**
  * Spy class to capture entities number
  */
class CellEliminationSystemSpy() extends CellsEliminationSystem {
  def entitiesSize:Int = entities.size
}

class TestCellsEliminationSystem extends FunSuite{
  val spawner = SpawnerComponent(false)
  val ce: CellEntity = CellBuilder().build
  val pce = PlayerCellEntity(CellBuilder().build, spawner)
  test("Cell elimination") {
    val system = new CellEliminationSystemSpy()
    pce.getDimensionComponent.radius_(system.radiusThreshold+1)
    ce.getDimensionComponent.radius_(system.radiusThreshold-1)
    assert(system.entitiesSize == 0)
    EntityManager.add(ce)
    system.update()
    assert(system.entitiesSize == 0)
    EntityManager.add(pce)
    system.update()
    assert(system.entitiesSize == 1)
    pce.getDimensionComponent.radius_(system.radiusThreshold-1)
    system.update()
    assert(system.entitiesSize == 0)
  }
}
