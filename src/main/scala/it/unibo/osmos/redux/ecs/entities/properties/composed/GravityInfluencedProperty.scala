package it.unibo.osmos.redux.ecs.entities.properties.composed

import it.unibo.osmos.redux.ecs.entities.properties.basic.{Acceleration, Position}

/** Trait representing the properties needed by an entity to be influenced by gravitational forces */
trait GravityInfluencedProperty extends Acceleration with Position {}
