package it.unibo.osmos.redux.multiplayer.players

case class BasicPlayer(private val username: String, private val playerInfo: PlayerInfo) extends AbstractPlayer(username) {

  /**
    * Gets the player info
    * @return The player info
    */
  def getInfo: PlayerInfo = playerInfo

  /**
    * Default empty constructor for serialization
    * @return The BasicPlayer instance with default values
    */
  def this() = this("", new PlayerInfo())
}
