package it.unibo.osmos.redux.main.ecs.systems

import it.unibo.osmos.redux.main.ecs.entities.EMEvents.{EntityCreated, EntityDeleted}
import it.unibo.osmos.redux.main.ecs.entities._

import scala.collection.mutable.ListBuffer

/**
  * Abstract system of generic entity
  */
abstract class System[T>:Entity] (val entityManager: Observable, val priority: Int) extends Observer{

  protected var entities: ListBuffer[T] = ListBuffer()

  entityManager.subscribe(this, Class[T])

  override def notify(event: EMEvents.EntityManagerEvent): Unit = event match {
    case event: EntityCreated => entities += event.entity
    case event: EntityDeleted => entities -= event.entity
  }

  def update()
}
