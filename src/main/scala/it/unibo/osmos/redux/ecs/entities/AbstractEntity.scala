package it.unibo.osmos.redux.ecs.entities

import java.util.UUID

/** Abstract class representing an ECS Entity */
abstract class AbstractEntity extends Property {

  /** Gets the UUID for this entity
    *
    * @return UUID for this entity
    */
  def getUUID: UUID
}
