package it.unibo.osmos.redux.mvc.view.drawables

import it.unibo.osmos.redux.ecs.entities.EntityType
import it.unibo.osmos.redux.utils.Point

/** Wrapper of any entity that must be drawn
  *
  * @param center     the entity center
  * @param radius     the entity radius
  * @param speed      the entity speed
  * @param entityType the entity type
  */
case class DrawableWrapper(center: Point, radius: Double, speed: (Double, Double), entityType: EntityType.Value) {}
