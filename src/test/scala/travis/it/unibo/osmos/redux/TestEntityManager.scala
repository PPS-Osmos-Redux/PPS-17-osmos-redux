package it.unibo.osmos.redux

import it.unibo.osmos.redux.ecs.components._
import it.unibo.osmos.redux.ecs.entities._
import it.unibo.osmos.redux.ecs.entities.builders.CellBuilder
import it.unibo.osmos.redux.ecs.entities.properties.composed.DeathProperty
import it.unibo.osmos.redux.ecs.systems.AbstractSystem
import org.scalatest.FunSuite

/**
  * Spy class to capture entities number
  */
case class SystemSpy() extends AbstractSystem[DeathProperty] {
  override def update(): Unit = ???
  def entitiesNumber: Int = entities.size
}

class TestEntityManager extends FunSuite{
  val spawner = SpawnerComponent(false)
  val ce: CellEntity = new CellBuilder().build
  val pce = PlayerCellEntity(new CellBuilder().build,spawner)
  test("Add and remove entity") {
    val systemSpy = SystemSpy()
    EntityManager.add(pce)
    assert(systemSpy.entitiesNumber == 1)
    EntityManager.add(ce)
    assert(systemSpy.entitiesNumber == 2)
    EntityManager.delete(pce)
    assert(systemSpy.entitiesNumber == 1)
    EntityManager.delete(ce)
    assert(systemSpy.entitiesNumber == 0)
    EntityManager.clear()
    EntityManager.add(ce)
    assert(systemSpy.entitiesNumber == 0)
  }
}
