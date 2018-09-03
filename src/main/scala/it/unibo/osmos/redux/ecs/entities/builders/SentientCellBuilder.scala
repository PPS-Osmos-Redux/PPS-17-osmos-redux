package it.unibo.osmos.redux.ecs.entities.builders
import it.unibo.osmos.redux.ecs.components
import it.unibo.osmos.redux.ecs.components.SpawnerComponent
import it.unibo.osmos.redux.ecs.entities.{EntityType, SentientCellEntity}

case class SentientCellBuilder() extends CellBuilder {
  private var spawner = components.SpawnerComponent(true)

  def withSpawner(spawner: SpawnerComponent): SentientCellBuilder = {
    this.spawner = spawner.copy()
    this
  }

  def withSpawner(canSpawn: Boolean): SentientCellBuilder = {
    this.spawner = SpawnerComponent(canSpawn)
    this
  }

  override def build: SentientCellEntity = {
    withEntityType(EntityType.Sentient)
    SentientCellEntity(super.build, spawner)
  }
}
