package it.unibo.osmos.redux.multiplayer.players

import akka.actor.ActorRef

case class ReferablePlayer(private val username: String, private val playerInfo: PlayerInfo, private val actorRef: ActorRef) extends AbstractPlayer(username) {

  def getInfo: PlayerInfo = playerInfo

  def getRef: ActorRef = actorRef

  def toBasicPlayer: BasicPlayer = BasicPlayer(username, playerInfo)
}
