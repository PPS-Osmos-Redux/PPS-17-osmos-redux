package it.unibo.osmos.redux

import it.unibo.osmos.redux.ecs.entities._
import it.unibo.osmos.redux.ecs.entities.builders.{CellBuilder, PlayerCellBuilder}
import it.unibo.osmos.redux.ecs.entities.properties.composed.{InputProperty, MovableProperty}
import it.unibo.osmos.redux.ecs.systems.AbstractSystem2
import org.scalatest.{BeforeAndAfter, FunSuite}

import scala.collection.mutable.ListBuffer

case class FakeSystem2()
  extends AbstractSystem2[MovableProperty, InputProperty] {

  override def update(): Unit = ???

  def getEntitiesOfFirstType: ListBuffer[MovableProperty] = entities

  def getEntitiesOfSecondType: ListBuffer[InputProperty] = entitiesSecondType
}

class TestSystem2 extends FunSuite with BeforeAndAfter {

  after(EntityManager.clear())

  test("A system initially has no entity") {
    val fakeSystem = FakeSystem2()
    assert(fakeSystem.getEntitiesOfFirstType.isEmpty)
    assert(fakeSystem.getEntitiesOfSecondType.isEmpty)
  }

  test("After add one entity of first type, the system have one entity of first type") {
    val fakeSystem = FakeSystem2()
    EntityManager.add(new CellBuilder().build)
    assert(fakeSystem.getEntitiesOfFirstType.size == 1)
    assert(fakeSystem.getEntitiesOfSecondType.isEmpty)
  }

  test("An entity of both types is found in both lists") {
    val fakeSystem = FakeSystem2()
    EntityManager.add(PlayerCellBuilder().build)
    assert(fakeSystem.getEntitiesOfFirstType.size == 1)
    assert(fakeSystem.getEntitiesOfSecondType.size == 1)
  }
}
