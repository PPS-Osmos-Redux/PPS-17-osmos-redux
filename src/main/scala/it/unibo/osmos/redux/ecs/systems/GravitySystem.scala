package it.unibo.osmos.redux.ecs.systems

import it.unibo.osmos.redux.ecs.components.EntityType
import it.unibo.osmos.redux.ecs.entities.EMEvents.{EntityCreated, EntityDeleted}
import it.unibo.osmos.redux.ecs.entities._
import it.unibo.osmos.redux.utils.{MathUtils, Point}

import scala.collection.mutable.ListBuffer

case class GravitySystem() extends AbstractSystemWithTwoTypeOfEntity[MovableProperty, GravityProperty]() {

  override def getGroupProperty: Class[MovableProperty] = classOf[MovableProperty]

  override protected def getGroupPropertySecondType: Class[GravityProperty] = classOf[GravityProperty]

  override def update(): Unit = for (
    gravityEntity <- entitiesSecondType; //for each gravity entity
    entity <- entities; //for each movable entity
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
    acceleration.vector.x_(acceleration.vector.x + unitVector.x*gravityAcceleration)
    acceleration.vector.y_(acceleration.vector.y + unitVector.y*gravityAcceleration)
  }

  private def getTypeOfForce(typeOfForce: EntityType.Value): Double = typeOfForce match {
    case EntityType.Attractive => 1
    case EntityType.Repulse => -1
    case _ => 0
  }
}
