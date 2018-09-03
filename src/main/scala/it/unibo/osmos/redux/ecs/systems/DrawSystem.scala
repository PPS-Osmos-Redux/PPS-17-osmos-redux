package it.unibo.osmos.redux.ecs.systems

import it.unibo.osmos.redux.ecs.entities._
import it.unibo.osmos.redux.mvc.view.drawables.{DrawableWrapper, EntitiesDrawer}

/**
  * System to draw all the entity
  * @param entitiesDrawer entitiesDrawer for communicate the entities to the view
  */
case class DrawSystem(entitiesDrawer: EntitiesDrawer) extends AbstractSystem2[DrawableProperty, PlayerCellEntity] {

  override def update(): Unit = entitiesDrawer.drawEntities(getPlayerEntity, getEntities)

  private def getPlayerEntity: Option[DrawableWrapper] =
    entitiesSecondType find (e => e.getVisibleComponent.isVisible &&
      e.getUUID == entitiesDrawer.getPlayerUUID) map(p => drawablePropertyToDrawableWrapper(p))

  private def getEntities: List[DrawableWrapper] =
    entities filter(e => e.getVisibleComponent.isVisible) map(e => drawablePropertyToDrawableWrapper(e)) toList

  private def drawablePropertyToDrawableWrapper(entity: DrawableProperty): DrawableWrapper =
    DrawableWrapper(entity.getPositionComponent.point,
                    entity.getDimensionComponent.radius,
                    (entity.getSpeedComponent.vector.x, entity.getSpeedComponent.vector.y),
                    entity.getTypeComponent.typeEntity)
}
