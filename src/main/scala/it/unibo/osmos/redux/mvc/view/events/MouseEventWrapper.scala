package it.unibo.osmos.redux.mvc.view.events

import java.util.UUID

import it.unibo.osmos.redux.utils.Point

/**
  * Case class which wraps a mouse event
  *
  * @param uuid the uuid of the entity linked with this event
  * @param point the coordinates of the clicked point
  */
case class MouseEventWrapper(uuid: UUID, point: Point) extends EventWrapper {

  def this(uuid: String, point: Point) = this(UUID.fromString(uuid), point)
}

