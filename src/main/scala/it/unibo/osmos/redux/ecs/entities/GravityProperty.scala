package it.unibo.osmos.redux.ecs.entities

/** Trait representing the properties needed by an entity to exercise gravitational forces */
trait GravityProperty extends Position with Mass with Type {}
