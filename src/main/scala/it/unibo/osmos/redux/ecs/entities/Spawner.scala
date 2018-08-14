package it.unibo.osmos.redux.ecs.entities

import it.unibo.osmos.redux.ecs.components.SpawnerComponent

trait Spawner extends Property {
  /**
    * Gets the Spawner Component
    *
    * @return the Spawner Component
    */
  def getSpawnerComponent: SpawnerComponent
}
