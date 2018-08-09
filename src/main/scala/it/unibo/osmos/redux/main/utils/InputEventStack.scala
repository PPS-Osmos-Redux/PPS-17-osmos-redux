package it.unibo.osmos.redux.main.utils

import scalafx.scene.input.MouseEvent

import scala.collection.mutable

/**
  * Singleton that stores all input events.
  */
object InputEventStack {
  var queue: mutable.ListBuffer[MouseEvent] = mutable.ListBuffer()

  /**
    * Pushes a new event into the stack
    * @param event The event
    */
  def push(event: MouseEvent): Unit = {
    queue += event
  }

  /**
    * Pops the last event from the stack
    * @return Optional of a event
    */
  def pop(): Option[MouseEvent] = {
    if (queue.nonEmpty) None() else Some(queue.last)
  }

  /**
    * Pops all events from the stack
    * @return
    */
  def popAll(): Seq[MouseEvent] = {
    queue
  }
}
