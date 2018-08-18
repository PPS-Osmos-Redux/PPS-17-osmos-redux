package it.unibo.osmos.redux.mvc.view.events

import scala.reflect.ClassTag

/**
  * A basic listener to a EventWrapper
 *
  * @tparam T the EventWrapper class or subclass
  */
trait EventWrapperListener[T <: EventWrapper] {

  /**
    * Called on a event T type
    * @param event the event
    */
  def onEvent(event: T)
}
