package it.unibo.osmos.redux.ecs.entities.builders

import it.unibo.osmos.redux.ecs.components
import it.unibo.osmos.redux.ecs.components.SpawnerComponent
import it.unibo.osmos.redux.ecs.entities.{EntityType, PlayerCellEntity}

case class PlayerCellBuilder() extends EntityBuilder[PlayerCellEntity] {

  private var spawner = components.SpawnerComponent(false)

  /**
    * Sets the spawner.
    * @param canSpawn If the entity spawner can spawn or not.
    * @return The entity builder.
    */
  def withSpawner(canSpawn: Boolean): PlayerCellBuilder = {
    this.spawner = SpawnerComponent(canSpawn)
    this
  }

  /**
    * Sets the spawner.
    * @param spawner The spawner component.
    * @return The entity builder.
    */
  def withSpawner(spawner: SpawnerComponent): PlayerCellBuilder = {
    this.spawner = spawner.copy()
    this
  }

  override def build: PlayerCellEntity = {
    withEntityType(EntityType.Controlled)
    PlayerCellEntity(buildBaseCell(), spawner)
  }
}
