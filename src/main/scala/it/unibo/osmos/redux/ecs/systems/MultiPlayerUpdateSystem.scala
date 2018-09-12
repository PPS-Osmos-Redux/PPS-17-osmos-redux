package it.unibo.osmos.redux.ecs.systems

import it.unibo.osmos.redux.ecs.entities.properties.composed.DrawableProperty
import it.unibo.osmos.redux.multiplayer.server.ServerActor.UpdateGame
import it.unibo.osmos.redux.multiplayer.server.{Server, ServerState}
import it.unibo.osmos.redux.mvc.view.drawables.DrawableEntity

/** System that sends the entities to draw to all connected clients.
  *
  * @param server The server used to send message to clients.
  */
case class MultiPlayerUpdateSystem(server: Server) extends AbstractSystem[DrawableProperty] {

  override def update(): Unit = if (server.getState == ServerState.Game) server.broadcastMessage(UpdateGame(getEntities))

  private def getEntities: Seq[DrawableEntity] =
    entities filter (_.getVisibleComponent.isVisible) map wrapToDrawable

  private def wrapToDrawable(entity: DrawableProperty): DrawableEntity =
    new DrawableEntity(entity.getUUID, entity.getPositionComponent.point, entity.getDimensionComponent.radius,
      (entity.getSpeedComponent.vector.x, entity.getSpeedComponent.vector.y), entity.getTypeComponent.typeEntity)
}
