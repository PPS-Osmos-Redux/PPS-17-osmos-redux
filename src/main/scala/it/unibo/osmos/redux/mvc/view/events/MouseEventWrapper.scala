package it.unibo.osmos.redux.mvc.view.events

import it.unibo.osmos.redux.utils.Point

/** Case class which wraps a mouse event
  *
  * @param point the coordinates of the clicked point
  * @param uuid  the uuid of the entity linked with this event, if None set empty string to prevent any.
  */
case class MouseEventWrapper(point: Point, uuid: String = "") extends EventWrapper {}

