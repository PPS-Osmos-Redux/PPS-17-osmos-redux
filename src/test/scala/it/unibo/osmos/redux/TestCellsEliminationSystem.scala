package it.unibo.osmos.redux

import it.unibo.osmos.redux.ecs.components._
import it.unibo.osmos.redux.ecs.entities.{CellEntity, EntityManager, PlayerCellEntity}
import org.scalatest.FunSuite
import it.unibo.osmos.redux.ecs.systems.CellsEliminationSystem
import it.unibo.osmos.redux.utils.Point



class TestCellsEliminationSystem extends FunSuite{
  val acceleration = AccelerationComponent(1, 1)
  val collidable = CollidableComponent(true)
  val speed = SpeedComponent(4, 0)
  val dimensionGreater = DimensionComponent(5)
  val dimensionLower = DimensionComponent(5)
  val position = PositionComponent(Point(0, 0))
  val notVisible = VisibleComponent(false)
  val typeEntity = TypeComponent(EntityType.Material)
  val spawner = SpawnerComponent(false)
  val pce = PlayerCellEntity(acceleration,collidable,dimensionGreater,position,speed,notVisible,typeEntity,spawner)
  val ce = CellEntity(acceleration,collidable,dimensionLower,position,speed,notVisible,typeEntity)
  test("Cell elimination") {
    val system = CellsEliminationSystem(0)
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
