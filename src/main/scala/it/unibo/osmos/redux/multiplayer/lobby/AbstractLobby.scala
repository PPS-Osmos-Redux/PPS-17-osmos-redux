package it.unibo.osmos.redux.multiplayer.lobby

import it.unibo.osmos.redux.multiplayer.players.Player
import it.unibo.osmos.redux.mvc.view.context.{LevelContext, LobbyContext}

abstract class AbstractLobby[T <: Player](protected val lobbyContext: LobbyContext) extends Lobby[T] {

  /**
    * Notify the interface that the game is started passing the context of the level to play.
    * @param levelContext The level context
    */
  def startGame(levelContext: LevelContext): Unit = ??? //TODO: call lobby startgame lobbyContext.startGame(levelContext)
}
