package it.unibo.osmos.redux.ecs.entities.properties.composed

import it.unibo.osmos.redux.ecs.entities.properties.basic.{Dimension, Type}

/** Trait representing the properties needed by an entity to be no longer alive */
trait DeathProperty extends Dimension with Type {}
