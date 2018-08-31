package it.unibo.osmos.redux.multiplayer.lobby

import it.unibo.osmos.redux.multiplayer.players.Player
import it.unibo.osmos.redux.mvc.view.components.multiplayer.User
import it.unibo.osmos.redux.mvc.view.context.LobbyContext

case class GameLobby[T <: Player](private val lobbyContext: LobbyContext) extends AbstractLobby[T](lobbyContext) {

  override def addPlayer(player: T): Unit = {
    if (getPlayer(player.getUsername).nonEmpty) throw new IllegalArgumentException("Cannot add player to lobby because the username is already specified")
    players += (player.getUsername -> player)
    notifyUserAdded(new User(player, false))
  }

  override def addPlayers(players: T*): Unit = players foreach addPlayer

  override def getPlayer(username: String): Option[T] = players get username

  override def getPlayers: Seq[T] = players.values.toList

  override def removePlayer(username: String): Unit = {
    notifyUserRemoved(new User(players(username), false))
    players -= username
  }
}

