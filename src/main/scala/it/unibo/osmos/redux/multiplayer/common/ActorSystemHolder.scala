package it.unibo.osmos.redux.multiplayer.common

import akka.actor.{ActorRef, ActorSystem, Address, PoisonPill}
import it.unibo.osmos.redux.multiplayer.client.{Client, ClientActor}
import it.unibo.osmos.redux.multiplayer.server.{Server, ServerActor}
import it.unibo.osmos.redux.utils.Constants

object ActorSystemHolder {

  /** Actor System variable, lazily initialized */
  private lazy val system: ActorSystem = {
    initialized = true
    ActorSystem(Constants.MultiPlayer.ActorSystemName, ActorSystemConfigFactory.load())
  }
  /** Determines whether the lazy system variable have been initialized or not */
  private var initialized: Boolean = false

  /** Gets the actor system.
    *
    * @return The actor system.
    */
  def getSystem: ActorSystem = system

  /** Gets the actor system network information.
    *
    * @return The network information of the system as Address object.
    */
  def systemAddress: Address = AddressExtension(system).address

  /** Creates a new ClientActor that refers to the input client object
    *
    * @param client The input client object that the new actor will refer to.
    * @return The ActorRef
    */
  def createActor(client: Client): ActorRef = system.actorOf(ClientActor.props(client), Constants.MultiPlayer.ClientActorName)

  /** Creates a new ServerActor that refers to the input server object
    *
    * @param server The input server object that the new actor will refer to.
    * @return The ActorRef
    */
  def createActor(server: Server): ActorRef = system.actorOf(ServerActor.props(server), Constants.MultiPlayer.ServerActorName)

  /** Stops an actor.
    *
    * @param actorRef The actor ref.
    */
  def stopActor(actorRef: ActorRef): Unit = system stop actorRef

  /** Clears all the actors of the actor system. */
  def clearActors(): Unit = system.actorSelection("/user/*") ! PoisonPill

  /** Kills this instance. */
  def kill(): Unit = if (initialized) system.terminate(); initialized = false
}
