package it.unibo.osmos.redux.ecs.components

import it.unibo.osmos.redux.utils.Point

/**
  * Represents a spawn action of a spawner.
  * @param position The initial position of the entity to spawn
  * @param dimension The dimension of the entity to spawn
  * @param speed The initial speed of the entity to spawn
  */
case class SpawnAction(var position: Point, var dimension: Double, var speed: Double) {
}
