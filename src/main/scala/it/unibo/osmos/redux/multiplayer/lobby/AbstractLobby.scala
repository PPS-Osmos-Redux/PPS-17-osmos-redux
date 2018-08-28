package it.unibo.osmos.redux.multiplayer.lobby

import it.unibo.osmos.redux.multiplayer.players.Player
import it.unibo.osmos.redux.mvc.view.components.multiplayer.User
import it.unibo.osmos.redux.mvc.view.context.{LobbyContext, MultiPlayerLevelContext}
import it.unibo.osmos.redux.mvc.view.events._

abstract class AbstractLobby[T <: Player](private val lobbyContext: LobbyContext) extends Lobby[T] {

  /**
    * Notify the interface that the game is started passing the context of the level to play.
    * @param levelContext The level context
    */
  def startGame(levelContext: MultiPlayerLevelContext): Unit = lobbyContext.notifyLobbyEvent(LobbyEventWrapper(StartGame(levelContext), null))

  /**
    * Notify the interface that the lobby have been closed by the server.
    */
  def leaveLobby(): Unit = lobbyContext.notifyLobbyEvent(LobbyEventWrapper(AbortLobby, null))

  /**
    * Notify the lobby context that a new user have been added to the lobby.
    * @param user The user
    */
  def notifyUserAdded(user: User): Unit = lobbyContext.notify(LobbyEventWrapper(UserAdded, user))

  /**
    * Notify the lobby context that a user have been removed from the lobby.
    * @param user The user
    */
  def notifyUserRemoved(user: User): Unit = lobbyContext.notify(LobbyEventWrapper(UserRemoved, user))
}
