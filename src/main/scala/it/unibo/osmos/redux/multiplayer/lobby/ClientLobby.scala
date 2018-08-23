package it.unibo.osmos.redux.multiplayer.lobby

import it.unibo.osmos.redux.multiplayer.players.BasicPlayer

case class ClientLobby() extends Lobby[BasicPlayer] {

  override def addPlayer(player: BasicPlayer): Unit = {
    if (getPlayer(player.getUsername).nonEmpty) throw new IllegalArgumentException("Cannot add player to lobby because the username is already specified")
    players += (player.getUsername -> player)
  }

  override def addPlayers(players: BasicPlayer*): Unit = players foreach addPlayer

  override def getPlayer(username: String): Option[BasicPlayer] = players get username

  override def getPlayers: Seq[BasicPlayer] = players.values.toList

  override def removePlayer(username: String): Unit = players -= username
}

