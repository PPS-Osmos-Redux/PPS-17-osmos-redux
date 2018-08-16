package it.unibo.osmos.redux

import it.unibo.osmos.redux.ecs.components._
import it.unibo.osmos.redux.ecs.entities._
import it.unibo.osmos.redux.ecs.systems.{AbstractSystemWithTwoTypeOfEntity, DrawSystem}
import it.unibo.osmos.redux.utils.Point
import org.scalatest.{BeforeAndAfter, FunSuite}

import scala.collection.mutable.ListBuffer


case class FakeSystemWithTwoTypeOfEntity(override val priority: Int)
  extends AbstractSystemWithTwoTypeOfEntity[MovableProperty, InputProperty](priority) {

  override protected def getGroupProperty: Class[_ <: Property] = classOf[MovableProperty]

  override protected def getGroupPropertySecondType: Class[_ <: Property] = classOf[InputProperty]

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
  val typeEntity = TypeComponent(EntityType.Material)

  after(EntityManager.clear())

  test("A system initially has no entity"){
    val fakeSystem = FakeSystemWithTwoTypeOfEntity(1)
    assert(fakeSystem.getEntitiesOfFirstType.isEmpty)
    assert(fakeSystem.getEntitiesOfSecondType.isEmpty)
  }

  test("After add one entity of first type, the system have one entity of first type"){
    val fakeSystem = FakeSystemWithTwoTypeOfEntity(1)
    val ce = CellEntity(acceleration, collidable, dimension, position, speed, visible, typeEntity)
    EntityManager.add(ce)
    assert(fakeSystem.getEntitiesOfFirstType.size == 1)
    assert(fakeSystem.getEntitiesOfSecondType.isEmpty)
  }

  test("An entity of both types is found in both lists"){
    val fakeSystem = FakeSystemWithTwoTypeOfEntity(1)
    EntityManager.add(PlayerCellEntity(acceleration, collidable, dimension, position, speed, visible, typeEntity))
    assert(fakeSystem.getEntitiesOfFirstType.size == 1)
    assert(fakeSystem.getEntitiesOfSecondType.size == 1)
  }
}
