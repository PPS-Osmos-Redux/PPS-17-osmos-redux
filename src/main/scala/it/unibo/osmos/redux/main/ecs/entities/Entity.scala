package it.unibo.osmos.redux.main.ecs.entities

import java.util.UUID

/**
  * Entity base trait
  */
trait Entity {

  def getUUID: UUID
}

object Entity {
  def apply(): Entity = EntityImpl()

  private case class EntityImpl() extends Entity {
    private val EntityUUID: UUID = UUID.randomUUID()

    override def getUUID: UUID = EntityUUID
  }

}
