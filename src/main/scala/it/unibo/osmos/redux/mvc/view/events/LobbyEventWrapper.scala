package it.unibo.osmos.redux.mvc.view.events

import it.unibo.osmos.redux.mvc.controller.levels.structure.LevelInfo
import it.unibo.osmos.redux.mvc.view.components.multiplayer.User
import it.unibo.osmos.redux.mvc.view.context.MultiPlayerLevelContext

/** Possible LobbyEvents */
sealed trait LobbyEvent extends EventWrapper

/** LobbyEvent. A user has been added to the lobby */
case object UserAdded extends LobbyEvent

/** LobbyEvent. A user has been removed from the lobby */
case object UserRemoved extends LobbyEvent

/** LobbyEvent. The game must be started.
  *
  * @param multiPlayerLevelContext the provided multiplayer level context
  * @param levelInfo the provided levelInfo
  */
case class StartGame(multiPlayerLevelContext: MultiPlayerLevelContext, levelInfo: LevelInfo) extends LobbyEvent

/** LobbyEvent. The lobby has been aborted */
case object AbortLobby extends LobbyEvent

/** Case class wrapping a LobbyEvent related to a User
  *
  * @param lobbyEvent the lobby event
  * @param user       the user the event refers to
  */
case class LobbyEventWrapper(lobbyEvent: LobbyEvent, user: Option[User]) extends EventWrapper
