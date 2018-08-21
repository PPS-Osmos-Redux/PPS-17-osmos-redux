package it.unibo.osmos.redux

import it.unibo.osmos.redux.ecs.components._
import it.unibo.osmos.redux.ecs.entities._
import it.unibo.osmos.redux.ecs.systems.AbstractSystemWithThreeTypeOfEntity
import it.unibo.osmos.redux.utils.Point
import org.scalatest.{BeforeAndAfter, FunSuite}

import scala.collection.mutable.ListBuffer

case class FakeSystemWithThreeTypeOfEntity()
  extends AbstractSystemWithThreeTypeOfEntity[MovableProperty, InputProperty, GravityProperty] {

  override protected def getGroupProperty: Class[MovableProperty] = classOf[MovableProperty]

  override protected def getGroupPropertySecondType: Class[InputProperty] = classOf[InputProperty]

  override protected def getGroupPropertyThirdType: Class[GravityProperty] = classOf[GravityProperty]

  override def update(): Unit = ???

  def getEntitiesOfFirstType: ListBuffer[MovableProperty] = entities

  def getEntitiesOfSecondType: ListBuffer[InputProperty] = entitiesSecondType

  def getEntitiesOfThirdType: ListBuffer[GravityProperty] = entitiesThirdType
}

class TestSystemWithThreeTypeOfEntity extends FunSuite with BeforeAndAfter {

  val acceleration = AccelerationComponent(1, 1)
  val collidable = CollidableComponent(true)
  val speed = SpeedComponent(4, 0)
  val dimension = DimensionComponent(5)
  val position = PositionComponent(Point(0, 0))
  val visible = VisibleComponent(true)
  val typeEntity = TypeComponent(EntityType.Material)
  val spawner = SpawnerComponent(false)
  val typeGravity = TypeComponent(EntityType.Attractive)
  val specificWeight = SpecificWeightComponent(1)
  val cellEntity = CellEntity(acceleration, collidable, dimension, position, speed, visible, typeEntity)
  val playerEntity = PlayerCellEntity(acceleration, collidable, dimension, position, speed, visible, typeEntity,spawner)
  val gravityEntity = GravityCellEntity(acceleration, collidable, dimension, position, speed, visible, typeEntity, specificWeight)

  after(EntityManager.clear())

  test("A system initially has no entity"){
    val fakeSystem = FakeSystemWithThreeTypeOfEntity()
    assert(fakeSystem.getEntitiesOfFirstType.isEmpty)
    assert(fakeSystem.getEntitiesOfSecondType.isEmpty)
    assert(fakeSystem.getEntitiesOfThirdType.isEmpty)
  }

  test("After add one entity of first type, the system have one entity of first type"){
    val fakeSystem = FakeSystemWithThreeTypeOfEntity()
    EntityManager.add(cellEntity)
    assert(fakeSystem.getEntitiesOfFirstType.size == 1)
    assert(fakeSystem.getEntitiesOfSecondType.isEmpty)
    assert(fakeSystem.getEntitiesOfThirdType.isEmpty)
  }

  test("An entity of first and second types is found in both lists"){
    val fakeSystem = FakeSystemWithThreeTypeOfEntity()
    EntityManager.add(playerEntity)
    assert(fakeSystem.getEntitiesOfFirstType.size == 1)
    assert(fakeSystem.getEntitiesOfSecondType.size == 1)
    assert(fakeSystem.getEntitiesOfThirdType.isEmpty)
  }

  test("An entity of first and third types is found in both lists"){
    val fakeSystem = FakeSystemWithThreeTypeOfEntity()
    EntityManager.add(gravityEntity)
    assert(fakeSystem.getEntitiesOfFirstType.size == 1)
    assert(fakeSystem.getEntitiesOfSecondType.isEmpty)
    assert(fakeSystem.getEntitiesOfThirdType.size == 1)
  }

  test("Add an entity and remove it, left all list empty"){
    val fakeSystem = FakeSystemWithThreeTypeOfEntity()
    EntityManager.add(gravityEntity)
    assert(fakeSystem.getEntitiesOfFirstType.size == 1)
    assert(fakeSystem.getEntitiesOfSecondType.isEmpty)
    assert(fakeSystem.getEntitiesOfThirdType.size == 1)
    EntityManager.delete(gravityEntity)
    assert(fakeSystem.getEntitiesOfFirstType.isEmpty)
    assert(fakeSystem.getEntitiesOfSecondType.isEmpty)
    assert(fakeSystem.getEntitiesOfThirdType.isEmpty)
  }
}
