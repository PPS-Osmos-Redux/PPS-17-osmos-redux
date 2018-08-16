package it.unibo.osmos.redux

import it.unibo.osmos.redux.ecs.components._
import it.unibo.osmos.redux.ecs.entities.{CellBuilder, EntityManager, PlayerCellEntity, Position}
import it.unibo.osmos.redux.ecs.systems.SpawnSystem
import it.unibo.osmos.redux.utils.Point
import org.scalatest.FunSuite

class TestSpawnSystem extends FunSuite {

  test("SpawnSystem should not spawn entities if the spawner is disabled") {
    val system = SpawnSystem()
    val pce = PlayerCellEntity(CellBuilder(), SpawnerComponent(true))

    pce.getSpawnerComponent.canSpawn_(false)
    EntityManager.add(pce)
    system.update()

    assert(EntityManager.filterEntities(classOf[Position]).size == 1)
  }

  test("SpawnSystem should not spawn entities if the are now spawn actions") {
    val system = SpawnSystem()
    val pce = PlayerCellEntity(CellBuilder(), SpawnerComponent(true))

    pce.getSpawnerComponent.clearActions()
    EntityManager.add(pce)
    system.update()

    assert(EntityManager.filterEntities(classOf[Position]).size == 1)
  }

  test("SpawnSystem should spawn entities with the correct components") {
    val system = SpawnSystem()
    val pce = PlayerCellEntity(CellBuilder(), SpawnerComponent(false))


    val pos = PositionComponent(Point(100,0))
    val speed = SpeedComponent(34, 12)
    val dim = DimensionComponent(50)

    pce.getSpawnerComponent.enqueueActions(SpawnAction(pos, dim, speed))
    EntityManager.add(pce)
    system.update()

    val entities = EntityManager.filterEntities(classOf[Position])

    assert(EntityManager.filterEntities(classOf[Position]).size == 2)
  }

}
