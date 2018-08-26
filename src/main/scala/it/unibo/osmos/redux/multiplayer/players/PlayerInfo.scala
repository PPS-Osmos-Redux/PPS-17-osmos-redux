package it.unibo.osmos.redux.multiplayer.players

case class PlayerInfo(address: String, port: Int) {

  /**
    * Default empty constructor for serialization
    * @return The PlayerInfo instance with default values
    */
  def this() = this("", 0)
}
