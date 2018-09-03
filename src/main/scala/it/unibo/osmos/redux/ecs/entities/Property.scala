package it.unibo.osmos.redux.ecs.entities

/** Property base trait */
trait Property {

  /** Gets the UUID of this entity
    *
    * @return The uuid.
    */
  def getUUID: String
}
