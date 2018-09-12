package it.unibo.osmos.redux.utils

import it.unibo.osmos.redux.mvc.view.events.MouseEventWrapper

import scala.collection.mutable

/** Singleton that stores all input events. */
object InputEventQueue {

  var queue: mutable.Queue[MouseEventWrapper] = mutable.Queue()

  /** Enqueues one or more events into the queue.
    *
    * @param events The events to add
    */
  def enqueue(events: MouseEventWrapper*): Unit = {
    queue.enqueue(events: _*)
  }

  /** Dequeues one event from the queue.
    *
    * @return Optional of a event
    */
  def dequeue(): Option[MouseEventWrapper] = {
    if (queue.nonEmpty) Some(queue.dequeue()) else None
  }

  /** Dequeue all events from the queue
    *
    * @return The list of all saved events
    */
  def dequeueAll(): Seq[MouseEventWrapper] = {
    queue.dequeueAll(_ => true)
  }
}
