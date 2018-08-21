package it.unibo.osmos.redux.ecs.entities

/** Trait representing the properties needed by an entity
  * to be seen as an enemy by the sentient cells
  */
trait SentientEnemyProperty extends Position with Speed with Dimension with Type {}
