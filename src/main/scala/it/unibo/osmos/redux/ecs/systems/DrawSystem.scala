package it.unibo.osmos.redux.ecs.systems

import it.unibo.osmos.redux.ecs.entities._
import it.unibo.osmos.redux.mvc.view.levels.LevelContext
import it.unibo.osmos.redux.mvc.view.drawables.DrawableWrapper

/**
  * System to draw all the entity
  * @param levelContext levelContext for communicate the entities to the view
  */
case class DrawSystem(levelContext: LevelContext) extends AbstractSystemWithTwoTypeOfEntity[DrawableProperty, PlayerCellEntity] {

  override def getGroupProperty: Class[_ <: Property] = classOf[DrawableProperty]

  override protected def getGroupPropertySecondType: Class[_ <: Property] = classOf[PlayerCellEntity]

  override def update(): Unit = levelContext.drawEntities(getPlayerEntity, getEntities)

  private def getPlayerEntity: Option[DrawableWrapper] =
    entitiesSecondType find (p => p.getVisibleComponent.isVisible())  map(p => drawablePropertyToDrawableWrapper(p))

  private def getEntities: List[DrawableWrapper] =
    entities filter(e => e.getVisibleComponent.isVisible()) map(e => drawablePropertyToDrawableWrapper(e)) toList

  private def drawablePropertyToDrawableWrapper(entity: DrawableProperty): DrawableWrapper =
    DrawableWrapper(entity.getPositionComponent.point,
                    entity.getDimensionComponent.radius,
                    (entity.getSpeedComponent.speedX, entity.getSpeedComponent.speedY),
                    entity.getTypeComponent.typeEntity)
}
