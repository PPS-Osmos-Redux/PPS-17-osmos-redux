package it.unibo.osmos.redux.multiplayer.players

import akka.actor.ActorRef

case class ReferablePlayer(private val username: String, private val playerInfo: PlayerInfo,
                           private val actorRef: ActorRef) extends AbstractPlayer(username) {

  private var uuid: String = ""

  /**
    * Gets the UUID of the player.
    * @return The UUID
    */
  def getUUID: String = uuid

  /**
    * Sets the UUID of the player.
    * @param uuid The UUID
    */
  def setUUID(uuid: String): Unit = this.uuid = uuid

  /**
    * Gets basic player info.
    * @return The player info
    */
  def getInfo: PlayerInfo = playerInfo

  /**
    * Gets the actor ref of the player.
    * @return The actor ref
    */
  def getRef: ActorRef = actorRef

  /**
    * Creates a BasicPlayer object from this instance.
    * @return The basic player object
    */
  def toBasicPlayer: BasicPlayer = BasicPlayer(username, playerInfo)
}
