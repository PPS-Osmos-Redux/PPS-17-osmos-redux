package it.unibo.osmos.redux.ecs.entities

/**
  * Trait representing the properties needed by an entity to be collidable
  */
trait CollidableProperty extends Position with Dimension with Speed with Collidable with Acceleration {

}
