package it.unibo.osmos.redux.ecs.systems

import it.unibo.osmos.redux.ecs.entities.EMEvents.{EntityCreated, EntityDeleted}
import it.unibo.osmos.redux.ecs.entities._
import it.unibo.osmos.redux.ecs.entities.properties.basic.Property

import scala.collection.mutable.ListBuffer
import scala.reflect.ClassTag

/** Abstract system of generic entity */
abstract class AbstractSystem[T <: Property : ClassTag]() extends Observer with System {

  /** The entity list with property of type T */
  protected val entities: ListBuffer[T] = ListBuffer()

  EntityManager.subscribe(this, implicitly[ClassTag[T]].runtimeClass.asInstanceOf[Class[T]])

  override def notify(event: EMEvents.EntityManagerEvent): Unit = event match {
    case event: EntityCreated => entities += event.entity.asInstanceOf[T]
    case event: EntityDeleted => entities -= event.entity.asInstanceOf[T]
  }

}
