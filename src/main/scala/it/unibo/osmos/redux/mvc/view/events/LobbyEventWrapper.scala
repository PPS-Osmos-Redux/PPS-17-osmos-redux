package it.unibo.osmos.redux.mvc.view.events

import it.unibo.osmos.redux.mvc.view.components.multiplayer.User

/**
  * Possible LobbyEvents
  */
sealed trait LobbyEvent extends EventWrapper
case object UserAdded extends LobbyEvent
case object UserRemoved extends LobbyEvent

/**
  * Case class wrapping a LobbyEvent related to a User
  * @param lobbyEvent the lobby event
  * @param user the user the event refers to
  */
case class LobbyEventWrapper(lobbyEvent: LobbyEvent, user: User)
