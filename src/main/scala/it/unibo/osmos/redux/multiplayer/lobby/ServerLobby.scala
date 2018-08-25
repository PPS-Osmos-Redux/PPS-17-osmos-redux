package it.unibo.osmos.redux.multiplayer.lobby

import it.unibo.osmos.redux.multiplayer.players.ReferablePlayer
import it.unibo.osmos.redux.mvc.view.context.LobbyContext

case class ServerLobby(override val lobbyContext: LobbyContext) extends AbstractLobby[ReferablePlayer](lobbyContext) {

  override def addPlayer(player: ReferablePlayer): Unit = {
    if (getPlayer(player.getUsername).nonEmpty) throw new IllegalArgumentException("Cannot add player to lobby because the username is already specified")
    players += (player.getUsername -> player)
  }

  override def addPlayers(players: ReferablePlayer*): Unit = players foreach addPlayer

  override def getPlayer(username: String): Option[ReferablePlayer] = players get username

  override def getPlayers: Seq[ReferablePlayer] = players.values.toList

  override def removePlayer(username: String): Unit = players -= username
}
