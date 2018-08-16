package it.unibo.osmos.redux.ecs.systems

import it.unibo.osmos.redux.ecs.entities.{CollidableProperty, Property}
import it.unibo.osmos.redux.utils.MathUtils

case class CollisionSystem() extends AbstractSystem[CollidableProperty] {

  //the percentage of mass that an entity can acquire from another during a collision in a tick
  private val massExchangeRate = 0.02
  //constants that controls how much deceleration is applied to an entity when colliding with another one
  private val decelerationAmount = 0.1
  //constant that define the initial acceleration of a steady entity when a collision occurs
  private val initialAcceleration = 0.001

  override def getGroupProperty: Class[_ <: Property] = classOf[CollidableProperty]

  override def update(): Unit = {
    for {
      (e1, xIndex) <- entities.zipWithIndex
      (e2, yIndex) <- entities.zipWithIndex
      if xIndex < yIndex //skip useless double checks
      overlap = computeOverlap(e1, e2)
      if overlap > 0 //check if they overlap (collide)
    } yield applyCollisionEffects(e1, e2, overlap)
  }

  /**
    * Computes the overlap between two entities.
    * @param e1 The first entity
    * @param e2 The second entity
    * @return The overlap amount.
    */
  private def computeOverlap(e1: CollidableProperty, e2: CollidableProperty): Double = {
    val maxDist = MathUtils.distanceBetweenPoints(e1.getPositionComponent.point, e2.getPositionComponent.point)
    val currDist = e1.getDimensionComponent.radius + e2.getDimensionComponent.radius
    if (maxDist < currDist) currDist - maxDist else 0
  }

  /**
    * Applies collision effects to two entities that collide with each other.
    * @param e1 The first entity
    * @param e2 The second entity
    * @param overlap The overlap amount
    */
  private def applyCollisionEffects(e1: CollidableProperty, e2: CollidableProperty, overlap: Double): Unit = {
    val (bigEntity, smallEntity) = if (e1.getDimensionComponent.radius > e2.getDimensionComponent.radius) (e1, e2) else (e2, e1)

    //exchange mass between the two entities
    exchangeMass(bigEntity, smallEntity, overlap)

    //apply deceleration to both entities, proportionally to their size
    decelerateEntity(smallEntity, decelerationAmount)
    decelerateEntity(bigEntity, decelerationAmount)
  }

  /**
    * Exchanges mass from the small entity to the big one (modify entity radius).
    * @param bigEntity The big entity
    * @param smallEntity The small entity
    * @param overlap The overlap amount
    */
  private def exchangeMass(bigEntity: CollidableProperty, smallEntity: CollidableProperty, overlap: Double): Unit = {
    //decrease small entity radius by the overlap amount
    smallEntity.getDimensionComponent.radius_(smallEntity.getDimensionComponent.radius - overlap)

    val exchangedRadiusValue = smallEntity.getDimensionComponent.radius * massExchangeRate
    val bigRadius = bigEntity.getDimensionComponent.radius
    val tinyRadius = smallEntity.getDimensionComponent.radius

    //apply exchange between the two entities
    bigEntity.getDimensionComponent.radius_(bigRadius + exchangedRadiusValue)
    smallEntity.getDimensionComponent.radius_(tinyRadius - exchangedRadiusValue)
  }

  /**
    * Applies deceleration to the input entity.
    * @param entity The entity to slow down
    * @param percentage The percentage of deceleration to apply
    */
  private def decelerateEntity(entity: CollidableProperty, percentage: Double): Unit = {
    val accel = entity.getAccelerationComponent

    //gain acceleration even if the entity is still
    if (accel.accelerationX == 0) {
      entity.getAccelerationComponent.accelerationX_(initialAcceleration * percentage)
      entity.getAccelerationComponent.accelerationY_(initialAcceleration * percentage)
    } else {
      entity.getAccelerationComponent.accelerationX_(accel.accelerationX - accel.accelerationX * percentage)
      entity.getAccelerationComponent.accelerationY_(accel.accelerationY - accel.accelerationY * percentage)
    }
  }
}
