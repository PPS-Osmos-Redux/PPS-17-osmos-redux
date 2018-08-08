package it.unibo.osmos.redux.main.ecs.entities

import scala.annotation.tailrec
import scala.collection.mutable

/**
  * EntityManager events
  */
object EMEvents {
  /**
    * Event of creation
    * @param entity new entity created
    * @return the creation event
    */
  def entityCreated(entity: Entity):EntityCreated = EntityCreated(entity)

  /**
    * Event of cancellation
    * @param entity the entity deleted
    * @return the delete event
    */
  def entityDeleted(entity: Entity):EntityDeleted = EntityDeleted(entity)

  sealed trait EntityManagerEvent {
    val entity:Entity
  }
  private case class EntityCreated(override val entity: Entity) extends EntityManagerEvent
  private case class EntityDeleted(override val entity: Entity) extends EntityManagerEvent
}

import EMEvents._

/**
  * Define an Observer interface for entity manager
  */
trait Observer {
  def notify(event:EntityManagerEvent):Unit
}

/**
  * Define an observable interface for entity manager
  */
trait Observable {
  /**
    * Adds new observer to the collection of observers
    * @param observer observer reference
    * @param managedEntities interface of managed entities
    */
  def subscribe(observer: Observer, managedEntities: EntityManager.ManagedEntity):Unit
}

/**
  * Define the methods for manage an entities collection
  */
trait EntityManager {
  type ManagedEntity = Class[_ <: Entity]
  type ObserverEntry = (Observer, ManagedEntity)
  /**
    * Adds the new entity to the collection
    * @param entity the new entity
    */
  def add(entity: Entity)

  /**
    * Removes the entity from the collection
    * @param entity the entity to be eliminated
    */
  def delete(entity: Entity)

  /**
    * Gives all the entities which extends managedEntity interface
    * @param entityInterface target entity interface
    * @return a list of entities
    */
  def filterEntities(entityInterface: ManagedEntity):List[Entity]
}

/**
  * Entity manager who manage the system entities and notify events to observers
  */
object EntityManager extends EntityManager with Observable {
  private var observers: List[ObserverEntry] = List()
  private var entities:mutable.Set[Entity] = mutable.Set()

  private def getInterfaces(entity: Entity):Array[Class[_]] = entity.getClass.getInterfaces

  private def extedsInterface(ent: Entity, entityInt: EntityManager.ManagedEntity):Boolean =
                                              getInterfaces(ent) contains entityInt

  @tailrec
  private def notifyEvent(observers:List[ObserverEntry],emEvent: EntityManagerEvent):Unit =
    observers match {
    case Nil =>
    case (obs,entityInt)::t if extedsInterface(emEvent.entity,entityInt) =>
                                                    obs.notify(emEvent); notifyEvent(t,emEvent)
    case _::t => notifyEvent(t,emEvent)
  }

  override def add(entity: Entity): Unit = {
    entities += entity
    notifyEvent(observers,EMEvents.entityCreated(entity))
  }

  override def delete(entity: Entity): Unit = {
    entities remove entity
    notifyEvent(observers,EMEvents.entityDeleted(entity))
  }

  override def subscribe(observer: Observer, managedEntities: ManagedEntity): Unit =
                          observers = (observer, managedEntities) :: observers

  override def filterEntities(entityInterface: EntityManager.ManagedEntity): List[Entity] =
    entities.filter(ent => extedsInterface(ent, entityInterface)).toList
}
