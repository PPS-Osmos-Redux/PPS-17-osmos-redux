package it.unibo.osmos.redux.ecs.systems

import it.unibo.osmos.redux.ecs.entities.EntityType
import it.unibo.osmos.redux.ecs.entities.properties.composed.{GravityProperty, MovableProperty}
import it.unibo.osmos.redux.utils.MathUtils

case class GravitySystem() extends AbstractSystem2[MovableProperty, GravityProperty]() {

  override def update(): Unit = for (
    gravityEntity <- entitiesSecondType; //for each gravity entity
    entity <- entities; //for each movable entity
    if !entity.equals(gravityEntity); //with entity not equal gravityEntity
    if !entity.getPositionComponent.point.equals(gravityEntity.getPositionComponent.point) //with center of entity not equal of center of gravityEntity(theory impossible)
  ) yield updateAcceleration(gravityEntity, entity)


  private def updateAcceleration(gravityProperty: GravityProperty, movableProperty: MovableProperty): Unit = {
    val gravityCenter = gravityProperty.getPositionComponent.point
    val entityCenter = movableProperty.getPositionComponent.point
    val distance = MathUtils.euclideanDistanceSq(gravityCenter, entityCenter)
    val typeOfForce = getTypeOfForce(gravityProperty.getTypeComponent.typeEntity)
    val gravityAcceleration = (gravityProperty.getMassComponent.mass / distance) * typeOfForce
    val unitVector = MathUtils.unitVector(gravityCenter, entityCenter)
    val acceleration = movableProperty.getAccelerationComponent
    acceleration.vector_(acceleration.vector add (unitVector multiply gravityAcceleration))
  }

  private def getTypeOfForce(typeOfForce: EntityType.Value): Double = typeOfForce match {
    case EntityType.Attractive => 1
    case EntityType.Repulsive => -1
    case _ => 0
  }
}
