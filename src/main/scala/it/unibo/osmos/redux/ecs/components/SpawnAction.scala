package it.unibo.osmos.redux.ecs.components

/**
  * Represents a spawn action of a spawner.
  * @param position The initial position of the entity to spawn
  * @param dimension The dimension of the entity to spawn
  * @param speed The initial speed of the entity to spawn
  */
case class SpawnAction(var position: PositionComponent, var dimension: DimensionComponent, var speed: SpeedComponent) {
}
