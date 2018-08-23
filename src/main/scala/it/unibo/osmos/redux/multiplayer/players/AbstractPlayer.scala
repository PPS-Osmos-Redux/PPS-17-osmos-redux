package it.unibo.osmos.redux.multiplayer.players

abstract class AbstractPlayer(private val username: String) extends Player {

  override def getUsername: String = username
}
