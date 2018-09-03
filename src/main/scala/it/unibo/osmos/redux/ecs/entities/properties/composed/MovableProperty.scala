package it.unibo.osmos.redux.ecs.entities.properties.composed

import it.unibo.osmos.redux.ecs.entities.properties.basic.{Acceleration, Dimension, Position, Speed}

/** Trait representing the properties needed by an entity to be movable */
trait MovableProperty extends Acceleration with Position with Speed with Dimension {}
