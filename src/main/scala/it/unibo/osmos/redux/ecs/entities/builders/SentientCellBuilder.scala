package it.unibo.osmos.redux.ecs.entities.builders
import it.unibo.osmos.redux.ecs.components
import it.unibo.osmos.redux.ecs.components.SpawnerComponent
import it.unibo.osmos.redux.ecs.entities.{EntityType, SentientCellEntity}

case class SentientCellBuilder() extends EntityBuilder[SentientCellEntity] {

  private var spawner = components.SpawnerComponent(true)

  /**
    * Sets the spawner.
    * @param canSpawn If the entity spawner can spawn or not.
    * @return The entity builder.
    */
  def withSpawner(canSpawn: Boolean): SentientCellBuilder = {
    this.spawner = SpawnerComponent(canSpawn)
    this
  }

  /**
    * Sets the spawner.
    * @param spawner The spawner component.
    * @return The entity builder.
    */
  def withSpawner(spawner: SpawnerComponent): SentientCellBuilder = {
    this.spawner = spawner.copy()
    this
  }

  override def build: SentientCellEntity = {
    withEntityType(EntityType.Sentient)
    SentientCellEntity(buildBaseCell(), spawner)
  }
}
