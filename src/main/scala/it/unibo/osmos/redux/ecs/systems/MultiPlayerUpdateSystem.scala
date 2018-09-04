package it.unibo.osmos.redux.ecs.systems

import it.unibo.osmos.redux.ecs.entities.properties.composed.DrawableProperty
import it.unibo.osmos.redux.multiplayer.server.Server
import it.unibo.osmos.redux.multiplayer.server.ServerActor.UpdateGame
import it.unibo.osmos.redux.mvc.view.drawables.DrawableEntity

case class MultiPlayerUpdateSystem(server: Server) extends AbstractSystem[DrawableProperty] {

  override def update(): Unit = server.broadcastMessage(UpdateGame(getEntities))

  private def getEntities: Seq[DrawableEntity] =
    entities filter(_.getVisibleComponent.isVisible) map wrapToDrawable

  private def wrapToDrawable(entity: DrawableProperty): DrawableEntity =
    new DrawableEntity(entity.getUUID, entity.getPositionComponent.point, entity.getDimensionComponent.radius,
      (entity.getSpeedComponent.vector.x, entity.getSpeedComponent.vector.y), entity.getTypeComponent.typeEntity)
}
