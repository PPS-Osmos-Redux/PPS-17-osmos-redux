package it.unibo.osmos.redux.multiplayer.lobby

import it.unibo.osmos.redux.multiplayer.players.Player
import it.unibo.osmos.redux.mvc.controller.levels.structure.LevelInfo
import it.unibo.osmos.redux.mvc.view.components.multiplayer.User
import it.unibo.osmos.redux.mvc.view.context.{LobbyContext, MultiPlayerLevelContext}
import it.unibo.osmos.redux.mvc.view.events._

abstract class AbstractLobby[T <: Player](private val lobbyContext: LobbyContext) extends Lobby[T] {

  /** Notify to lobby context that the game is started passing the context of the level to play.
    *
    * @param levelContext The level context.
    * @param levelInfo    The level info.
    */
  def notifyGameStarted(levelContext: MultiPlayerLevelContext, levelInfo: LevelInfo): Unit = {
    lobbyContext.notify(LobbyEventWrapper(StartGame(levelContext, levelInfo), None))
  }

  /** Notify to lobby context that the lobby have been closed.
    *
    * @param byUser If the lobby have been closed by the user or not (optional, default true).
    */
  def notifyLobbyClosed(byUser: Boolean = true): Unit = {
    if (byUser) lobbyContext.notifyLobbyEvent(LobbyEventWrapper(AbortLobby, None))
    else lobbyContext.notify(LobbyEventWrapper(AbortLobby, None))
  }

  /** Notify the lobby context that a new user have been added to the lobby.
    *
    * @param user The user.
    */
  def notifyUserAdded(user: User): Unit = lobbyContext.notify(LobbyEventWrapper(UserAdded, Some(user)))

  /** Notify the lobby context that a user have been removed from the lobby.
    *
    * @param user The user.
    */
  def notifyUserRemoved(user: User): Unit = lobbyContext.notify(LobbyEventWrapper(UserRemoved, Some(user)))
}
