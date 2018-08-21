package it.unibo.osmos.redux.ecs.entities

/** Trait representing the properties needed by an entity
  * to be the target of the sentient cells
  */
trait SentientTargetProperty extends Position with Speed with Dimension {}
