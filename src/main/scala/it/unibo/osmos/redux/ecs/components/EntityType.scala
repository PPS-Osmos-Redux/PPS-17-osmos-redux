package it.unibo.osmos.redux.ecs.components

/**
  * Enumeration which holds the different entity types
  */
object EntityType extends Enumeration {

  /**
    * Basic entity type used by common entities
    */
  val Material, Attractive, Repulse, Sentient: EntityType.Value = Value
}
