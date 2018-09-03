package it.unibo.osmos.redux.ecs.entities.properties.composed

import it.unibo.osmos.redux.ecs.entities.properties.basic._

/** Trait representing the properties needed by an entity to be collidable */
trait CollidableProperty extends Position with Dimension with Speed with Collidable with Acceleration with Type {}
