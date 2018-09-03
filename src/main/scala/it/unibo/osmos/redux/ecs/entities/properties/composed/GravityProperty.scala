package it.unibo.osmos.redux.ecs.entities.properties.composed

import it.unibo.osmos.redux.ecs.entities.properties.basic.{Mass, Position, Type}

/** Trait representing the properties needed by an entity to exercise gravitational forces */
trait GravityProperty extends Position with Mass with Type {}
