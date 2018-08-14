package it.unibo.osmos.redux.ecs.systems

import it.unibo.osmos.redux.ecs.components.EntityType
import it.unibo.osmos.redux.ecs.entities.EMEvents.{EntityCreated, EntityDeleted}
import it.unibo.osmos.redux.ecs.entities._
import it.unibo.osmos.redux.utils.{MathUtils, Point}

import scala.collection.mutable.ListBuffer

case class GravitySystem(override val priority: Int) extends AbstractSystem[MovableProperty](priority) {

  //TODO: da qui in giù dovrebbe essere estratto in un sistema astratto con due tipi di entità
  private val gravityEntities: ListBuffer[GravityProperty] = ListBuffer()

  EntityManager.subscribe(this, classOf[GravityProperty])

  override def notify(event: EMEvents.EntityManagerEvent): Unit = {
    event.entity match {
      case _:GravityProperty =>
        event match {
          case event: EntityCreated if !gravityEntities.contains(event.entity) => gravityEntities += event.entity.asInstanceOf[GravityProperty]
          case event: EntityDeleted if gravityEntities.contains(event.entity)=> gravityEntities -= event.entity.asInstanceOf[GravityProperty]
          case _ => super.notify(event)
        }
      case _ => super.notify(event)
    }
  }
  //TODO: fine parte da estrarre

  override def getGroupProperty: Class[_ <: Property] = classOf[MovableProperty]

  override def update(): Unit = for (
    entity <- entities; //for each movable entity
    gravityEntity <- gravityEntities; //for each gravity entity
    if !entity.equals(gravityEntity); //with entity not equal gravityEntity
    if !entity.getPositionComponent.point.equals(gravityEntity.getPositionComponent.point) //with center of entity not equal of center of gravityEntity(theory impossible)
  ) yield updateAcceleration(gravityEntity, entity)


  private def updateAcceleration(gravityProperty: GravityProperty, movableProperty: MovableProperty): Unit = {
    val gravityCenter = gravityProperty.getPositionComponent.point
    val entityCenter = movableProperty.getPositionComponent.point
    val distance = Math.pow(MathUtils.euclideanDistance(gravityCenter, entityCenter),2)
    val typeOfForce = getTypeOfForce(gravityProperty.getTypeComponent.typeEntity)
    val gravityAcceleration = (gravityProperty.getMassComponent.mass / distance) *typeOfForce
    val unitVector = MathUtils.normalizePoint(Point(gravityCenter.x - entityCenter.x, gravityCenter.y - entityCenter.y))
    val acceleration = movableProperty.getAccelerationComponent
    acceleration.accelerationX_(acceleration.accelerationX + unitVector.x*gravityAcceleration)
    acceleration.accelerationY_(acceleration.accelerationY + unitVector.y*gravityAcceleration)
  }

  private def getTypeOfForce(typeOfForce: EntityType.Value): Double = typeOfForce match {
    case EntityType.Attractive => 1
    case EntityType.Repulse => -1
    case _ => 0
  }
}
