package it.unibo.osmos.redux.mvc.view.events

/**
  * A basic observer to a EventWrapper
 *
  * @tparam T the EventWrapper class or subclass
  */
trait EventWrapperObserver[T <: EventWrapper]{

  /**
    * Called on a event T type
    * @param event the event
    */
  def notify(event: T)
}
