package it.unibo.osmos.redux.mvc.view.events

/**
  * This trait models a generic EventWrapper source
  * @tparam T the EventWrapper type
  */
trait EventWrapperSource[T <: EventWrapper] {

  /**
    * This method registers a single event listener
    * @param eventListener the listener
    */
  def registerEventListener(eventListener: EventWrapperListener[T])

  /**
    * This method unregisters a single event listener
    * @param eventListener the listener
    */
  def unregisterEventListener(eventListener: EventWrapperListener[T])

  /**
    * This method pushes an event to the registered listener
    * @param event the event
    */
  def pushEvent(event: T)
}
