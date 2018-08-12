package it.unibo.osmos.redux.main.utils

import it.unibo.osmos.redux.main.mvc.view.events.MouseEventWrapper

import scala.collection.mutable

/**
  * Singleton that stores all input events.
  */
object InputEventStack {

  //TODO: handle race conditions (pops and pushes should be synchronized, but can effect performances)

  var stack: mutable.ListBuffer[MouseEventWrapper] = mutable.ListBuffer()

  /**
    * Pushes a new event into the stack
    * @param event The event
    */
  def push(event: MouseEventWrapper): Unit = {
    stack += event
  }

  /**
    * Pushes multiple events into the stack (following the order of the input collection)
    * @param events The collection of events
    */
  def pushAll(events: MouseEventWrapper*): Unit = {
    events foreach push
  }

  /**
    * Pops the last event from the stack
    * @return Optional of a event
    */
  def pop(): Option[MouseEventWrapper] = {
    if (stack.isEmpty) None else {
      val last = Some(stack.last)
      stack = stack.dropRight(1)
      last
    }
  }

  /**
    * Pops all events from the stack
    * @return The list of all saved events
    */
  def popAll(): Seq[MouseEventWrapper] = {
    val copy = stack.clone
    stack.clear
    copy
  }
}
