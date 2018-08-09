package it.unibo.osmos.redux.main.mvc.view.events

import it.unibo.osmos.redux.main.utils.Point

/**
  * Case class which wraps a mouse event
 *
  * @param point the coordinates of the clicked point
  */
case class MouseEventWrapper(point: Point) extends EventWrapper {}
