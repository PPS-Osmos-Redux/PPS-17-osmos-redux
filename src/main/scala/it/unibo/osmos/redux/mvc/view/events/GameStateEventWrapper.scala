package it.unibo.osmos.redux.mvc.view.events

/**
  * Possible game events
  */
sealed trait GameStateEventWrapper extends EventWrapper
case object GameWon extends GameStateEventWrapper
case object GameLost extends GameStateEventWrapper
case object GamePending extends GameStateEventWrapper
