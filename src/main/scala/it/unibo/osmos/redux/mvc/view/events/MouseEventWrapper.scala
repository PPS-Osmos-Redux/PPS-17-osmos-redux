package it.unibo.osmos.redux.mvc.view.events

import java.util.UUID

import it.unibo.osmos.redux.utils.Point

/**
  * Case class which wraps a mouse event
  *
  * @param point the coordinates of the clicked point
  * @param uuid the uuid of the entity linked with this event. If none, it is set randomly.
  */
case class MouseEventWrapper(point: Point, uuid: UUID = UUID.randomUUID()) extends EventWrapper {

  def this(point: Point, uuid: String) = this(point, UUID.fromString(uuid))

}

