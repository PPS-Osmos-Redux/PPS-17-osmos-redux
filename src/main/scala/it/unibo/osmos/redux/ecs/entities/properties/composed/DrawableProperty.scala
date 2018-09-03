package it.unibo.osmos.redux.ecs.entities.properties.composed

import it.unibo.osmos.redux.ecs.entities.properties.basic._

/** Trait representing the properties needed by an entity to be drawable */
trait DrawableProperty extends Property with Position with Dimension with Visible with Type with Speed {}
