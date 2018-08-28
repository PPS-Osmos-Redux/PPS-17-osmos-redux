package it.unibo.osmos.redux.multiplayer.players

/**
  * Represents a basic lobby player
  * @param username The username
  * @param address The address
  * @param port The port
  */
case class BasicPlayer(username: String, address: String, port: Int) extends Player
