package it.unibo.osmos.redux.mvc.view.drawables

import it.unibo.osmos.redux.ecs.components.EntityType
import it.unibo.osmos.redux.utils.Point

/**
  * Wrapper of any entity that must be drawn
  */
case class DrawableWrapper(center: Point, radius: Double, speed: (Double, Double), entityType: EntityType.Value) {}
