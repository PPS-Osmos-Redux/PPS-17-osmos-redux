package it.unibo.osmos.redux.ecs.systems

import it.unibo.osmos.redux.ecs.entities.{Property, Spawner}

case class SpawnSystem() extends AbstractSystem[Spawner] {

  override def getGroupProperty: Class[_ <: Property] = classOf[Spawner]

  /**
    * Performs an action on all the entities of the system
    */
  override def update(): Unit = {
    //TODO: for each Spawner get all SpawnActions and spawn entities
    //entities foreach(_ => _)
  }
}
