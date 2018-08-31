package it.unibo.osmos.redux.multiplayer.server

import it.unibo.osmos.redux.mvc.view.events.MouseEventWrapper

trait ClientInputEventObserver {

  /**
    * Signals the observer that a new event is available.
    * @param event The event.
    */
  def update(event: MouseEventWrapper)
}
