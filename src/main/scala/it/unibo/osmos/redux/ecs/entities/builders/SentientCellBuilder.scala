package it.unibo.osmos.redux.ecs.entities.builders
import it.unibo.osmos.redux.ecs.entities.{EntityType, SentientCellEntity}

case class SentientCellBuilder() extends CellBuilder {

  override def withEntityType(entityType: EntityType.Value): CellBuilder =
    throw new UnsupportedOperationException("Is not possible set entity type for SentientCellEntity")

  override def build: SentientCellEntity = {
    checkMultipleBuild()
    SentientCellEntity(acceleration, collidable, dimension, position, speed, visible)
  }
}
