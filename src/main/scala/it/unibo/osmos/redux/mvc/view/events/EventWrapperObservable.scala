package it.unibo.osmos.redux.mvc.view.events

/** This trait models a generic EventWrapper observable
  *
  * @tparam T the EventWrapper type
  */
trait EventWrapperObservable[T <: EventWrapper] {

  /** This method subscribes a single event observer
    *
    * @param eventWrapperObserver the observer
    */
  def subscribe(eventWrapperObserver: EventWrapperObserver[T])

  /** This method unsubscribes a single event observer
    *
    * @param eventWrapperObserver the observer
    */
  def unsubscribe(eventWrapperObserver: EventWrapperObserver[T])

}
