package it.unibo.osmos.redux.ecs.entities.builders

import it.unibo.osmos.redux.ecs.components
import it.unibo.osmos.redux.ecs.components.SpawnerComponent
import it.unibo.osmos.redux.ecs.entities.PlayerCellEntity

case class PlayerCellBuilder() extends CellBuilder {
  private var spawner = components.SpawnerComponent(false)

  def withSpawner(spawner: SpawnerComponent): PlayerCellBuilder = {
    this.spawner = spawner.copy()
    this
  }

  def withSpawner(canSpawn: Boolean): PlayerCellBuilder = {
    this.spawner = SpawnerComponent(canSpawn)
    this
  }

  override def build: PlayerCellEntity = {
    PlayerCellEntity(super.build, spawner)
  }
}
