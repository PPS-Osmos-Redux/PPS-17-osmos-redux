package it.unibo.osmos.redux

import it.unibo.osmos.redux.ecs.components._
import it.unibo.osmos.redux.ecs.entities._
import it.unibo.osmos.redux.ecs.entities.composed.MovableProperty
import it.unibo.osmos.redux.ecs.entities.properties.composed.{InputProperty, MovableProperty}
import it.unibo.osmos.redux.ecs.systems.{AbstractSystem2, DrawSystem}
import it.unibo.osmos.redux.utils.Point
import org.scalatest.{BeforeAndAfter, FunSuite}

import scala.collection.mutable.ListBuffer

case class FakeSystem2()
  extends AbstractSystem2[MovableProperty, InputProperty] {

  override def update(): Unit = ???

  def getEntitiesOfFirstType: ListBuffer[MovableProperty] = entities

  def getEntitiesOfSecondType: ListBuffer[InputProperty] = entitiesSecondType
}

class TestSystemWithTwoTypeOfEntity extends FunSuite with BeforeAndAfter {

  val acceleration = AccelerationComponent(1, 1)
  val collidable = CollidableComponent(true)
  val speed = SpeedComponent(4, 0)
  val dimension = DimensionComponent(5)
  val position = PositionComponent(Point(0, 0))
  val visible = VisibleComponent(true)
  val typeEntity = TypeComponent(EntityType.Matter)
  val spawner = SpawnerComponent(false)

  after(EntityManager.clear())

  test("A system initially has no entity"){
    val fakeSystem = FakeSystem2()
    assert(fakeSystem.getEntitiesOfFirstType.isEmpty)
    assert(fakeSystem.getEntitiesOfSecondType.isEmpty)
  }

  test("After add one entity of first type, the system have one entity of first type"){
    val fakeSystem = FakeSystem2()
    val ce = CellEntity(acceleration, collidable, dimension, position, speed, visible, typeEntity)
    EntityManager.add(ce)
    assert(fakeSystem.getEntitiesOfFirstType.size == 1)
    assert(fakeSystem.getEntitiesOfSecondType.isEmpty)
  }

  test("An entity of both types is found in both lists"){
    val fakeSystem = FakeSystem2()
    EntityManager.add(PlayerCellEntity(acceleration, collidable, dimension, position, speed, visible, spawner, typeEntity))
    assert(fakeSystem.getEntitiesOfFirstType.size == 1)
    assert(fakeSystem.getEntitiesOfSecondType.size == 1)
  }
}
