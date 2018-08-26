package it.unibo.osmos.redux.ecs.systems

import it.unibo.osmos.redux.ecs.entities.DrawableProperty
import it.unibo.osmos.redux.multiplayer.server.Server
import it.unibo.osmos.redux.mvc.view.drawables.DrawableWrapper

case class MultiPlayerUpdateSystem(server: Server) extends AbstractSystem[DrawableProperty] {

  override protected def getGroupProperty: Class[DrawableProperty] = classOf[DrawableProperty]

  override def update(): Unit = server.broadcastMessage(getEntities)

  private def getEntities: Seq[DrawableWrapper] =
    entities filter(_.getVisibleComponent.isVisible()) map wrapToDrawable

  private def wrapToDrawable(entity: DrawableProperty): DrawableWrapper =
    DrawableWrapper(entity.getPositionComponent.point, entity.getDimensionComponent.radius,
      (entity.getSpeedComponent.vector.x, entity.getSpeedComponent.vector.y), entity.getTypeComponent.typeEntity)
}
