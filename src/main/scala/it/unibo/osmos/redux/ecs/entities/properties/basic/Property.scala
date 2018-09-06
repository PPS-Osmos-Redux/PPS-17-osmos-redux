package it.unibo.osmos.redux.ecs.entities.properties.basic

/** Property base trait */
trait Property {

  /** Gets the UUID of this entity
    *
    * @return The uuid.
    */
  def getUUID: String
}
