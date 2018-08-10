package it.unibo.osmos.redux.main.utils

import scalafx.scene.input.MouseEvent

import scala.collection.mutable

/**
  * Singleton that stores all input events.
  */
object InputEventStack {

  var stack: mutable.ListBuffer[MouseEvent] = mutable.ListBuffer()

  /**
    * Pushes a new event into the stack
    * @param event The event
    */
  def push(event: MouseEvent): Unit = {
    stack += event
  }

  /**
    * Pops the last event from the stack
    * @return Optional of a event
    */
  def pop(): Option[MouseEvent] = {
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
  def popAll(): Seq[MouseEvent] = {
    val copy = stack.clone
    stack.clear
    copy
  }
}
