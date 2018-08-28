package it.unibo.osmos.redux.ecs.entities

/** Trait representing the properties needed by an entity to be drawable */
trait DrawableProperty extends Property with Position with Dimension with Visible with Type with Speed {}
