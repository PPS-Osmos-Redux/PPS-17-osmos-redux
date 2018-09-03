package it.unibo.osmos.redux.ecs.entities.properties.basic

import it.unibo.osmos.redux.ecs.components.SpawnerComponent

/** Trait representing the entity's spawn property */
trait Spawner extends Property {

  /** Gets the Spawner Component
    *
    * @return the Spawner Component
    */
  def getSpawnerComponent: SpawnerComponent
}
