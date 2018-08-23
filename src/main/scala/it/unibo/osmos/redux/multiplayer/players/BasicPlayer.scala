package it.unibo.osmos.redux.multiplayer.players

case class BasicPlayer(private val username: String, private val playerInfo: PlayerInfo) extends AbstractPlayer(username) {
  def getInfo: PlayerInfo = playerInfo
}
