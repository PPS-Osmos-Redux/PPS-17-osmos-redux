package it.unibo.osmos.redux.ecs.systems

import it.unibo.osmos.redux.ecs.entities._
import it.unibo.osmos.redux.ecs.entities.builders.CellBuilder
import it.unibo.osmos.redux.ecs.entities.properties.basic.Spawner

case class SpawnSystem() extends AbstractSystem[Spawner] {

  /**
    * Performs an action on all the entities of the system
    */
  override def update(): Unit = {
    entities foreach(e => {
      e.getSpawnerComponent.dequeueActions() foreach (a => {
        EntityManager.add(
          new CellBuilder()
            .collidable(true)
            .visible(true)
            .withSpeed(a.speed)
            .withDimension(a.dimension)
            .withPosition(a.position)
            .build)
      })
    })
  }
}
