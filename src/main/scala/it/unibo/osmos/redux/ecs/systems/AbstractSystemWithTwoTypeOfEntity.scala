package it.unibo.osmos.redux.ecs.systems

import it.unibo.osmos.redux.ecs.entities.EMEvents.{EntityCreated, EntityDeleted}
import it.unibo.osmos.redux.ecs.entities._

import scala.collection.mutable.ListBuffer

/**
  * Abstract system with two type of generic entity.
  * The lists of entity are not exclusive
  */
abstract class AbstractSystemWithTwoTypeOfEntity[T <:Property, R <:Property](override val priority: Int)
          extends AbstractSystem[T](priority) with Observer with System {

  protected var entitiesSecondType: ListBuffer[R] = ListBuffer()

  EntityManager.subscribe(this, getGroupPropertySecondType)
  protected def getGroupPropertySecondType: Class[_<:Property]

  override def notify(event: EMEvents.EntityManagerEvent): Unit = {
    event.entity match {
      case _:R if !entitiesSecondType.contains(event.entity)=>
        event match {
          case event: EntityCreated => entitiesSecondType += event.entity.asInstanceOf[R]
          case event: EntityDeleted => entitiesSecondType -= event.entity.asInstanceOf[R]
        }
      case _ => super.notify(event)
    }
  }

  /*event match {
    case event: EntityCreated if event.entity.isInstanceOf[R] => entitiesSecondType += event.entity.asInstanceOf[R]; println("Wrong:" )
    case event: EntityDeleted if event.entity.isInstanceOf[R] => entitiesSecondType -= event.entity.asInstanceOf[R]
    case _ => super.notify(event)
  }*/
}
