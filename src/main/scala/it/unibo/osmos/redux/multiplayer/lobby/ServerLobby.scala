package it.unibo.osmos.redux.multiplayer.lobby

import it.unibo.osmos.redux.multiplayer.players.ReferablePlayer
import it.unibo.osmos.redux.mvc.view.components.multiplayer.User
import it.unibo.osmos.redux.mvc.view.context.LobbyContext

case class ServerLobby(private val lobbyContext: LobbyContext) extends AbstractLobby[ReferablePlayer](lobbyContext) {

  override def addPlayer(player: ReferablePlayer): Unit = {
    if (getPlayer(player.getUsername).nonEmpty) throw new IllegalArgumentException("Cannot add player to lobby because the username is already specified")
    players += (player.getUsername -> player)
    notifyUserAdded(new User(player.toBasicPlayer, false))
  }

  override def addPlayers(players: ReferablePlayer*): Unit = players foreach addPlayer

  override def getPlayer(username: String): Option[ReferablePlayer] = players get username

  override def getPlayers: Seq[ReferablePlayer] = players.values.toList

  override def removePlayer(username: String): Unit = {
    notifyUserRemoved(new User(players(username).toBasicPlayer , false))
    players -= username
  }
}
