package it.unibo.osmos.redux.ecs.systems

import it.unibo.osmos.redux.ecs.entities.EMEvents.{EntityCreated, EntityDeleted}
import it.unibo.osmos.redux.ecs.entities._

import scala.collection.mutable.ListBuffer
import scala.reflect.ClassTag

/**
  * Abstract system with three type of generic entity.
  * The lists of entity are not exclusive
  */
abstract class AbstractSystemWithThreeTypeOfEntity[T <:Property, R <:Property: ClassTag, S <:Property: ClassTag]
          extends AbstractSystemWithTwoTypeOfEntity[T, R] with Observer with System {

  protected var entitiesThirdType: ListBuffer[S] = ListBuffer()

  EntityManager.subscribe(this, getGroupPropertyThirdType)
  protected def getGroupPropertyThirdType: Class[S]

  override def notify(event: EMEvents.EntityManagerEvent): Unit = {
    event.entity match {
      case _:S =>
        event match {
          case event: EntityCreated if !entitiesThirdType.contains(event.entity) => entitiesThirdType += event.entity.asInstanceOf[S]
          case event: EntityDeleted if entitiesThirdType.contains(event.entity)=> entitiesThirdType -= event.entity.asInstanceOf[S]
          //the event is already managed so the event is duplicated, perhaps it is for another type of entity
          case _ => super.notify(event)
        }
      case _ => super.notify(event)
    }
  }
}
