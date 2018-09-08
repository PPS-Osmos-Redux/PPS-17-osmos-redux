package it.unibo.osmos.redux.ecs.systems

import it.unibo.osmos.redux.ecs.entities._
import it.unibo.osmos.redux.ecs.entities.builders.CellBuilder
import it.unibo.osmos.redux.ecs.entities.properties.basic.Spawner

/** System managing the spawn of new entities */
case class SpawnSystem() extends AbstractSystem[Spawner] {

  override def update(): Unit = {
    entities foreach(e => {
      e.getSpawnerComponent.dequeueActions() foreach (a => {
        EntityManager.add(
          CellBuilder()
            .collidable(true)
            .visible(true)
            .withSpeed(a.speed)
            .withDimension(a.dimension)
            .withPosition(a.position)
            .buildCellEntity())
      })
    })
  }
}
