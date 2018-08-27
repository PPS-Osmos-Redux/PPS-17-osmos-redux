package it.unibo.osmos.redux.multiplayer.players

import akka.actor.ActorRef

/**
  * Lobby user with references
  * @param username The username
  * @param address The address
  * @param port The port
  * @param actorRef The actor reference
  */
case class ReferablePlayer(username: String, address: String, port: Int, actorRef: ActorRef) extends Player {

  /**
    * Secondary constructor.
    * @param basicPlayer The BasicPlayer.
    * @param actorRef The actor reference.
    * @return A new ReferablePlayer instance.
    */
  def this(basicPlayer: BasicPlayer, actorRef: ActorRef) = this(basicPlayer.username, basicPlayer.address, basicPlayer.port, actorRef)

  /**
    * The uuid
    */
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
    * Creates a BasicPlayer object from this instance.
    * @return The basic player object
    */
  def toBasicPlayer: BasicPlayer = BasicPlayer(username, address, port)
}
