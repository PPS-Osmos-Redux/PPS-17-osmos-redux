package it.unibo.osmos.redux.ecs.systems

import it.unibo.osmos.redux.ecs.entities._

case class SpawnSystem() extends AbstractSystem[Spawner] {

  override def getGroupProperty: Class[Spawner] = classOf[Spawner]

  /**
    * Performs an action on all the entities of the system
    */
  override def update(): Unit = {
    //TODO: for each Spawner get all SpawnActions and spawn entities
    entities foreach(e => {
      e.getSpawnerComponent.dequeueActions() foreach (a => {
        EntityManager.add(
          CellBuilder()
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
