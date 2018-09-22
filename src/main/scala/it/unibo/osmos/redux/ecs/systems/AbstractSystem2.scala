package it.unibo.osmos.redux.ecs.systems

import it.unibo.osmos.redux.ecs.entities.EMEvents.{EntityCreated, EntityDeleted}
import it.unibo.osmos.redux.ecs.entities._
import it.unibo.osmos.redux.ecs.entities.properties.basic.Property

import scala.collection.mutable.ListBuffer
import scala.reflect.ClassTag

/**
  * Abstract system with two type of generic entity.
  * The lists of entity are not exclusive
  */
abstract class AbstractSystem2[T <: Property : ClassTag, R <: Property : ClassTag] extends AbstractSystem[T] {

  /** The entity list with property of type R.
    * An entity with property of type T could be also in this list
    */
  protected val entitiesSecondType: ListBuffer[R] = ListBuffer()

  EntityManager.subscribe(this, implicitly[ClassTag[R]].runtimeClass.asInstanceOf[Class[R]])

  override def notify(event: EMEvents.EntityManagerEvent): Unit = {
    event.entity match {
      case _: R =>
        event match {
          case event: EntityCreated if !entitiesSecondType.contains(event.entity) => entitiesSecondType += event.entity.asInstanceOf[R]
          case event: EntityDeleted if entitiesSecondType.contains(event.entity) => entitiesSecondType -= event.entity.asInstanceOf[R]
          //the event is already managed so the event is duplicated, perhaps it is for the other type of entity
          case _ => super.notify(event)
        }
      case _ => super.notify(event)
    }
  }
}
