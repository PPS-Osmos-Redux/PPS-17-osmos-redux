package it.unibo.osmos.redux.mvc.view.drawables

import it.unibo.osmos.redux.ecs.entities.EntityType
import it.unibo.osmos.redux.utils.Point

/** DrawableWrapper extension which holds the entity UUID
  *
  * @param uuid       the entity UUID
  * @param center     the entity center
  * @param radius     the entity radius
  * @param speed      the entity speed
  * @param entityType the entity type
  */
class DrawableEntity(private val uuid: String, override val center: Point, override val radius: Double,
                     override val speed: (Double, Double), override val entityType: EntityType.Value)
  extends DrawableWrapper(center, radius, speed, entityType) {

  /** Gets the uuid of the entity.
    *
    * @return The uuid.
    */
  def getUUID: String = uuid
}
