package it.unibo.osmos.redux.ecs.systems

import it.unibo.osmos.redux.ecs.entities.DrawableProperty
import it.unibo.osmos.redux.multiplayer.server.Server
import it.unibo.osmos.redux.mvc.view.drawables.DrawableWrapper

case class MultiPlayerSystem(server: Server) extends AbstractSystem[DrawableProperty] {

  override protected def getGroupProperty: Class[DrawableProperty] = classOf[DrawableProperty]

  override def update(): Unit = server.updateClients(getEntities)

  private def getEntities: Seq[DrawableWrapper] =
    entities filter(_.getVisibleComponent.isVisible()) map drawablePropertyToDrawableWrapper

  private def drawablePropertyToDrawableWrapper(entity: DrawableProperty): DrawableWrapper =
    DrawableWrapper(entity.getPositionComponent.point, entity.getDimensionComponent.radius,
      (entity.getSpeedComponent.vector.x, entity.getSpeedComponent.vector.y), entity.getTypeComponent.typeEntity)
}
