package it.unibo.osmos.redux.multiplayer.players

/** Represents a basic lobby player
  *
  * @param username The username
  * @param address  The address
  * @param port     The port
  */
case class BasePlayer(private val username: String, private val address: String, private val port: Int) extends Player {

  override def getUsername: String = username

  override def getAddress: String = address

  override def getPort: Int = port
}
