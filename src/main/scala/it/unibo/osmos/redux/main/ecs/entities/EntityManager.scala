package it.unibo.osmos.redux.main.ecs.entities
import org.apache.commons.lang3.ClassUtils

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
  def entityCreated(entity: Property):EntityCreated = EntityCreated(entity)

  /**
    * Event of cancellation
    * @param entity the entity deleted
    * @return the delete event
    */
  def entityDeleted(entity: Property):EntityDeleted = EntityDeleted(entity)

  sealed trait EntityManagerEvent {
    val entity:Property
  }

  case class EntityCreated(override val entity: Property) extends EntityManagerEvent
  case class EntityDeleted(override val entity: Property) extends EntityManagerEvent
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
  type ManagedEntity = Class[_ <: Property]
  type ObserverEntry = (Observer, ManagedEntity)
  /**
    * Adds the new entity to the collection
    * @param entity the new entity
    */
  def add(entity: Property)

  /**
    * Removes the entity from the collection
    * @param entity the entity to be eliminated
    */
  def delete(entity: Property)

  /**
    * Gives all the entities which extends managedEntity interface
    * @param entityInterface target entity interface
    * @return a list of entities
    */
  def filterEntities(entityInterface: ManagedEntity):List[Property]

  /**
    * Clear internal collections for starting new level
    */
  def clear()
}
import scala.collection.JavaConverters._
/**
  * Entity manager who manage the system entities and notify events to observers
  */
object EntityManager extends EntityManager with Observable {
  private var observers: List[ObserverEntry] = List()
  private var entities:mutable.Set[Property] = mutable.Set()

  def getInterfaces(entity: Property):List[Class[_]] =
                                ClassUtils.getAllInterfaces(entity.getClass).asScala.toList

  def extendsInterface(ent: Property, entityInt: EntityManager.ManagedEntity):Boolean =
                                getInterfaces(ent) contains entityInt

  @tailrec
  private def notifyEvent(observers:List[ObserverEntry],emEvent: EntityManagerEvent):Unit =
    observers match {
      case Nil =>
      case (obs,entityInt)::t if extendsInterface(emEvent.entity,entityInt) =>
        obs.notify(emEvent); notifyEvent(t,emEvent)
      case _::t => notifyEvent(t,emEvent)
    }

  override def add(entity: Property): Unit = {
    entities += entity
    notifyEvent(observers,EMEvents.entityCreated(entity))
  }

  override def delete(entity: Property): Unit = {
    entities remove entity
    notifyEvent(observers,EMEvents.entityDeleted(entity))
  }

  override def subscribe(observer: Observer, managedEntities: ManagedEntity): Unit =
    observers = (observer, managedEntities) :: observers

  override def filterEntities(entityInterface: EntityManager.ManagedEntity): List[Property] =
    entities.filter(ent => extendsInterface(ent, entityInterface)).toList

  override def clear(): Unit = {
    observers = List()
    entities.clear()
  }
}
