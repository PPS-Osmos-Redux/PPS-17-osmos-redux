package it.unibo.osmos.redux.multiplayer.client

import it.unibo.osmos.redux.mvc.view.events.GameStateEventWrapper

trait GameStatusChangedObserver {

  /**
    * Signals to the observer that the game status has changed.
    * @param status The current status of the game.
    */
  def update(status: GameStateEventWrapper)
}
