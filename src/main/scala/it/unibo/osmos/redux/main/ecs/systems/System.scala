package it.unibo.osmos.redux.main.ecs.systems

import it.unibo.osmos.redux.main.ecs.entities.EMEvents.{EntityCreated, EntityDeleted}
import it.unibo.osmos.redux.main.ecs.entities._

import scala.collection.mutable.ListBuffer

/**
  * Abstract system of generic entity
  */
abstract class System[T <:Property](val priority: Int) extends Observer{

  protected var entities: ListBuffer[T] = ListBuffer()

  EntityManager.subscribe(this, getGroupProperty())
  def getGroupProperty(): Class[_<:Property]

  override def notify(event: EMEvents.EntityManagerEvent): Unit = event match {
    case event: EntityCreated => entities += event.entity.asInstanceOf[T]
    case event: EntityDeleted => entities -= event.entity.asInstanceOf[T]
  }

  def update(): Unit
}
