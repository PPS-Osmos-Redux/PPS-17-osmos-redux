package it.unibo.osmos.redux.mvc.view.events

/** Possible game events */
sealed trait GameStateEventWrapper extends EventWrapper
case object GameWon extends GameStateEventWrapper
case object GameLost extends GameStateEventWrapper
case object GamePending extends GameStateEventWrapper

/** Possible game events as a server */
sealed trait MultiPlayerGameStateEventWrapper extends GameStateEventWrapper
case object GameLostAsServer extends MultiPlayerGameStateEventWrapper
