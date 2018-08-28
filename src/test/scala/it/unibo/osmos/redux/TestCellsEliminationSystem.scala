package it.unibo.osmos.redux

import it.unibo.osmos.redux.ecs.components._
import it.unibo.osmos.redux.ecs.entities.builders.CellBuilder
import it.unibo.osmos.redux.ecs.entities.{CellEntity, EntityManager, PlayerCellEntity}
import org.scalatest.FunSuite
import it.unibo.osmos.redux.ecs.systems.CellsEliminationSystem
import it.unibo.osmos.redux.utils.Point

/**
  * Spy class to capture entities number
  */
class CellEliminationSystemSpy() extends CellsEliminationSystem {
  def entitiesSize:Int = entities.size
}

class TestCellsEliminationSystem extends FunSuite{
  val spawner = SpawnerComponent(false)
  val ce: CellEntity = new CellBuilder().build
  val pce = PlayerCellEntity(new CellBuilder().build,spawner)
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
