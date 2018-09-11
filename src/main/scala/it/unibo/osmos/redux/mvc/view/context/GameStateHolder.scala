package it.unibo.osmos.redux.mvc.view.context

import it.unibo.osmos.redux.mvc.view.events.{EventWrapperObserver, GameStateEventWrapper}

/**
  * Trait modelling an object which holds the current game state and reacts when it gets changed
  */
trait GameStateHolder extends EventWrapperObserver[GameStateEventWrapper] {

  /** A generic definition of the game state
    *
    * @return a GameStateEventWrapper
    */
  def gameCurrentState: GameStateEventWrapper

}
