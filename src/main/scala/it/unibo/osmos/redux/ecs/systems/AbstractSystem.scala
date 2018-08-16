package it.unibo.osmos.redux.ecs.systems

import it.unibo.osmos.redux.ecs.entities.EMEvents.{EntityCreated, EntityDeleted}
import it.unibo.osmos.redux.ecs.entities._
import it.unibo.osmos.redux.mvc.model.Level

import scala.collection.mutable.ListBuffer

/**
  * Abstract system of generic entity
  */
abstract class AbstractSystem[T <:Property](override val priority: Int) extends Observer with System {

  protected var entities: ListBuffer[T] = ListBuffer()

  EntityManager.subscribe(this, getGroupProperty)
  def getGroupProperty: Class[_<:Property]

  override def notify(event: EMEvents.EntityManagerEvent): Unit = event match {
    case event: EntityCreated => entities += event.entity.asInstanceOf[T]
    case event: EntityDeleted => entities -= event.entity.asInstanceOf[T]
  }

}
