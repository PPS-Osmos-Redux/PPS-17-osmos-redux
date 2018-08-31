package it.unibo.osmos.redux.ecs.entities

/**
  * Enumeration which holds the different entity types
  */
object EntityType extends Enumeration {

  /**
    * Basic entity type used by common entities
    */
  val Matter, AntiMatter, Attractive, Repulsive, Sentient, Controlled: EntityType.Value = Value

}
