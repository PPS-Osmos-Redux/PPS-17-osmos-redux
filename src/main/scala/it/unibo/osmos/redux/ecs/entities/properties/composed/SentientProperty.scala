package it.unibo.osmos.redux.ecs.entities.properties.composed

import it.unibo.osmos.redux.ecs.entities.properties.basic.Spawner

/** Trait representing the properties needed by an entity to be sentient */
trait SentientProperty extends CollidableProperty with Spawner {}
