package it.unibo.osmos.redux

import it.unibo.osmos.redux.ecs.components._
import it.unibo.osmos.redux.ecs.entities._
import it.unibo.osmos.redux.ecs.systems.SpawnSystem
import it.unibo.osmos.redux.utils.Point
import org.scalatest.FunSuite

class TestSpawnSystem extends FunSuite {

  test("SpawnSystem should not spawn entities if the spawner is disabled") {
    EntityManager.clear()

    val system = SpawnSystem()
    val pce = PlayerCellEntity(CellBuilder(), SpawnerComponent(true))

    pce.getSpawnerComponent.canSpawn_(false)
    EntityManager.add(pce)
    system.update()

    assert(EntityManager.filterEntities(classOf[Position]).size == 1)
  }

  test("SpawnSystem should not spawn entities if the are now spawn actions") {
    EntityManager.clear()

    val system = SpawnSystem()
    val pce = PlayerCellEntity(CellBuilder(), SpawnerComponent(true))

    pce.getSpawnerComponent.clearActions()
    EntityManager.add(pce)
    system.update()

    assert(EntityManager.filterEntities(classOf[Position]).size == 1)
  }

  test("SpawnSystem should spawn entities with the correct components") {
    EntityManager.clear()

    val system = SpawnSystem()
    val pce = PlayerCellEntity(CellBuilder(), SpawnerComponent(true))

    val pos = PositionComponent(Point(100,0))
    val speed = SpeedComponent(34, 12)
    val dim = DimensionComponent(50)

    pce.getSpawnerComponent.enqueueActions(SpawnAction(pos, dim, speed))
    EntityManager.add(pce)
    system.update()

    val entities = EntityManager.filterEntities(classOf[CellEntity])
    val spawnedEntity = entities.filterNot(e => e.isInstanceOf[PlayerCellEntity]).map(_.asInstanceOf[CellEntity]).headOption

    assert(entities.size == 2 && spawnedEntity.isDefined &&
      spawnedEntity.get.getPositionComponent == pos &&
      spawnedEntity.get.getSpeedComponent == speed &&
      spawnedEntity.get.getDimensionComponent == dim)
  }

}
