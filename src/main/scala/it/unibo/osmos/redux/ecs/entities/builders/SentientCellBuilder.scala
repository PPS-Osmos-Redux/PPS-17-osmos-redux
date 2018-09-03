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

  override def withEntityType(entityType: EntityType.Value): CellBuilder =
    throw new UnsupportedOperationException("Is not possible set entity type for SentientCellEntity")

  override def build: SentientCellEntity = {
    checkMultipleBuild()
    SentientCellEntity(acceleration, collidable, dimension, position, speed, visible, spawner)
  }
}
