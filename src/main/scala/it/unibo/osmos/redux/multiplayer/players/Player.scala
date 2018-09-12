package it.unibo.osmos.redux.multiplayer.players

/** Root Player trait */
trait Player {

  /** Gets the username.
    *
    * @return The username
    */
  def getUsername: String

  /** Gets the address.
    *
    * @return The address
    */
  def getAddress: String

  /** Gets the port.
    *
    * @return The port
    */
  def getPort: Int
}
