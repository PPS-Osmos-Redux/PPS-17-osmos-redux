package it.unibo.osmos.redux.ecs.components

import it.unibo.osmos.redux.ecs.entities.EntityType

/**
  * Component for entity's type
 *
  * @param typeEntity entity's type
  */
case class TypeComponent(typeEntity: EntityType.Value)
