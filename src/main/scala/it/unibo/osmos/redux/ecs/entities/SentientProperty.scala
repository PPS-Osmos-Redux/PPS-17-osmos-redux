package it.unibo.osmos.redux.ecs.entities

/** Trait representing the properties needed by an entity to be sentient */
trait SentientProperty extends Position with Speed with Acceleration with Dimension with Spawner{}
