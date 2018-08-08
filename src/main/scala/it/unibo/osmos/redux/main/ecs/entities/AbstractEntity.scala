package it.unibo.osmos.redux.main.ecs.entities

import java.util.UUID

/**
  * Abstract class representing an ECS Entity
  */
abstract class AbstractEntity extends Property {

  /**
    * Gets the UUID for this entity
    *
    * @return the UUID for this entity
    */
  def getUUID: UUID
}
