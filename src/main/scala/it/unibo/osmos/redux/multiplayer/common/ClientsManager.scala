package it.unibo.osmos.redux.multiplayer.common

import akka.actor.ActorRef

import scala.collection.concurrent.TrieMap
import scala.util.Random

trait ClientsManager {

  protected val clients: TrieMap[String, ActorRef] = TrieMap()

  /**
    * Generates the next temp id for a new client.
    * @return The next temp id to add a new client.
    */
  def nextTempID: String

  /**
    * Adds a new client.
    * @param tempID The temp id
    * @param actorRef The actor ref
    * @return True, if the operation is s; otherwise false.
    */
  def addClient(tempID: String, actorRef: ActorRef): Boolean

  /**
    * Gets and remove the specified client.
    * @param tempID The temp id of the client.
    * @return The Actor reference.
    */
  def getClient(tempID: String): Option[ActorRef]

  /**
    * Clears all clients from the manager.
    */
  def clearClients(): Unit
}

object ClientsManager {

  /**
    * Creates a new clients manager instance.
    * @param seed The random seed used to generate temp ids.
    * @return A new ClientsManager instance.
    */
  def apply(seed: Long = Long.MinValue): ClientsManager = ClientsManagerImpl(new Random(seed))

  private case class ClientsManagerImpl(private val random: Random) extends ClientsManager {

    override def nextTempID: String = random.alphanumeric.take(10).mkString

    override def addClient(tempID: String, actorRef: ActorRef): Boolean = clients.putIfAbsent(tempID, actorRef).isEmpty

    override def getClient(tempID: String): Option[ActorRef] = {
      val result = clients get tempID
      clients -= tempID
      result
    }

    override def clearClients(): Unit = clients.clear()
    }
}
