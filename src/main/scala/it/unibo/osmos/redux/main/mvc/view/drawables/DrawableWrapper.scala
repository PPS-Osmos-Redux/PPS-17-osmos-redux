package it.unibo.osmos.redux.main.mvc.view.drawables

import it.unibo.osmos.redux.main.utils.Point
import it.unibo.osmos.redux.main.ecs.components.EntityType

/**
  * Wrapper of any entity that must be drawn
  */
case class DrawableWrapper(center: Point, radius: Double, entityType: EntityType.Value) {}
