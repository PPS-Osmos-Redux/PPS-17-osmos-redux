package it.unibo.osmos.redux.main.mvc.view.drawables

import it.unibo.osmos.redux.main.utils.Point

/**
  * Wrapper of any entity that must be drawn
  */
//TODO: Add type enum
case class DrawableWrapper(center: Point, radius: Double) {}
