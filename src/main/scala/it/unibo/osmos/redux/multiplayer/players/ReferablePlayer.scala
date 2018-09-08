package it.unibo.osmos.redux.multiplayer.players

import akka.actor.ActorRef

trait Referable {

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
    * The actor ref.
    * @return The actor ref
    */
  def getActorRef: ActorRef
}

/**
  * Lobby user with references
  * @param username The username
  * @param address The address
  * @param port The port
  * @param actorRef The actor reference
  */
class ReferablePlayer(private val username: String, private val address: String, private val port: Int, private val actorRef: ActorRef) extends BasePlayer(username, address, port) with Referable {

  private var alive: Boolean = true

  /**
    * Secondary constructor.
    * @param basicPlayer The BasicPlayer.
    * @param actorRef The actor reference.
    * @return A new ReferablePlayer instance.
    */
  def this(basicPlayer: BasePlayer, actorRef: ActorRef) = this(basicPlayer.getUsername, basicPlayer.getAddress, basicPlayer.getPort, actorRef)

  override def getActorRef: ActorRef = actorRef

  /**
    * Creates a BasicPlayer object from this instance.
    * @return The basic player object
    */
  def toBasicPlayer: BasePlayer = BasePlayer(username, address, port)

  /**
    * Sets the liveness of the player.
    * @param isAlive If the player is alive or not.
    */
  def setLiveness(isAlive: Boolean): Unit = alive = isAlive

  /**
    * Determines whether the player is alive or not.
    * @return If the player is alive or not.
    */
  def isAlive: Boolean = alive
}
