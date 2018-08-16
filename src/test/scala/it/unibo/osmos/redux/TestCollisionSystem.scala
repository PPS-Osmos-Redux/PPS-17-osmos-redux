package it.unibo.osmos.redux

import it.unibo.osmos.redux.ecs.components._
import it.unibo.osmos.redux.ecs.entities.{CellEntity, EntityManager}
import it.unibo.osmos.redux.ecs.systems.CollisionSystem
import it.unibo.osmos.redux.utils.Point
import org.scalatest.FunSuite

class TestCollisionSystem extends FunSuite {

  val entity1 = CellEntity(AccelerationComponent(0,0), CollidableComponent(true), DimensionComponent(5),
    PositionComponent(Point(20, 30)), SpeedComponent(0, 0), VisibleComponent(true), TypeComponent(EntityType.Material))
  val entity2 = CellEntity(AccelerationComponent(0,0), CollidableComponent(true), DimensionComponent(2),
    PositionComponent(Point(60, 80)), SpeedComponent(0, 0), VisibleComponent(true), TypeComponent(EntityType.Material))

  test("CollisionSystem should not collide the entity with herself") {
    val system = CollisionSystem()

    val originalDim = entity1.getDimensionComponent
    val originalAccel = entity1.getAccelerationComponent

    EntityManager.add(entity1)

    system.update()

    assert(entity1.getDimensionComponent == originalDim && entity1.getAccelerationComponent == originalAccel)
  }

  test("CollisionSystem should not consider entities that do not have CollisionProperty") {
    val system = CollisionSystem()

    val position = Point(60, 80)
    entity1.getPositionComponent.point_(position)
    entity2.getCollidableComponent.setCollidable(true)
    entity2.getPositionComponent.point_(position)
    entity2.getCollidableComponent.setCollidable(false)

    val originalDim1 = entity1.getDimensionComponent
    val originalAccel1 = entity1.getAccelerationComponent
    val originalDim2 = entity2.getDimensionComponent
    val originalAccel2 = entity2.getAccelerationComponent

    EntityManager.add(entity1)
    EntityManager.add(entity2)

    system.update()

    assert(entity1.getDimensionComponent == originalDim1 && entity1.getAccelerationComponent == originalAccel1 &&
      entity2.getDimensionComponent == originalDim2 && entity2.getAccelerationComponent == originalAccel2)
  }

  test("CollisionSystem should not collide two entities if the distance between the centers is greater than the sum of their radii") {
    val system = CollisionSystem()

    val originalDim1 = entity1.getDimensionComponent
    val originalAccel1 = entity1.getAccelerationComponent
    val originalDim2 = entity2.getDimensionComponent
    val originalAccel2 = entity2.getAccelerationComponent

    EntityManager.add(entity1)
    EntityManager.add(entity2)

    system.update()

    assert(entity1.getDimensionComponent == originalDim1 && entity1.getAccelerationComponent == originalAccel1 &&
      entity2.getDimensionComponent == originalDim2 && entity2.getAccelerationComponent == originalAccel2)
  }

  test("CollisionSystem should collide two entities if the distance between the centers is less than the sum of their radii") {
    val system = CollisionSystem()

    entity1.getDimensionComponent.radius_(5)
    entity1.getPositionComponent.point_(Point(60, 80))
    entity1.getCollidableComponent.setCollidable(true)
    entity2.getDimensionComponent.radius_(2)
    entity2.getPositionComponent.point_(Point(66.9, 80))
    entity2.getCollidableComponent.setCollidable(true)

    val originalDim1 = DimensionComponent(entity1.getDimensionComponent.radius)
    val originalAccel1 = AccelerationComponent(entity1.getAccelerationComponent.accelerationX, entity1.getAccelerationComponent.accelerationY)
    val originalDim2 = DimensionComponent(entity2.getDimensionComponent.radius)
    val originalAccel2 = AccelerationComponent(entity2.getAccelerationComponent.accelerationX, entity2.getAccelerationComponent.accelerationY)

    EntityManager.add(entity1)
    EntityManager.add(entity2)

    system.update()

    assert(entity1.getDimensionComponent != originalDim1 && entity1.getAccelerationComponent != originalAccel1 &&
      entity2.getDimensionComponent != originalDim2 && entity2.getAccelerationComponent != originalAccel2)
  }
}
