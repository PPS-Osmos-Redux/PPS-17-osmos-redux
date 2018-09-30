package it.unibo.osmos.redux.mvc.view.events

/** Possible game events */
sealed trait GameStateEventWrapper extends EventWrapper

/** GameStateEventWrapper. The game has been won. */
case object GameWon extends GameStateEventWrapper

/** GameStateEventWrapper. The game has been lost. */
case object GameLost extends GameStateEventWrapper

/** GameStateEventWrapper. The game is in pending state */
case object GamePending extends GameStateEventWrapper

/** Possible game events as a server */
sealed trait MultiPlayerGameStateEventWrapper extends GameStateEventWrapper

/** GameStateEventWrapper. The game has been lost as a server. */
case object GameLostAsServer extends MultiPlayerGameStateEventWrapper
