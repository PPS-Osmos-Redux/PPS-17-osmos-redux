package it.unibo.osmos.redux.ecs.entities.builders
import it.unibo.osmos.redux.ecs.components.EntityType
import it.unibo.osmos.redux.ecs.entities.SentientCellEntity

case class SentientCellBuilder() extends CellBuilder {

  override def withEntityType(entityType: EntityType.Value): CellBuilder = throw UnsupportedOperationException

  override def build: SentientCellEntity = {
    checkMultipleBuild()
    SentientCellEntity(acceleration, collidable, dimension, position, speed, visible)
  }
}
