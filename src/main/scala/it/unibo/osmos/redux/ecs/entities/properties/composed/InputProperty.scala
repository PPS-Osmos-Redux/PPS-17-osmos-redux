package it.unibo.osmos.redux.ecs.entities.properties.composed

import it.unibo.osmos.redux.ecs.entities.properties.basic._

/** Trait representing the properties needed by an entity to handle input */
trait InputProperty extends Position with Speed with Acceleration with Spawner with Dimension {}
