package it.unibo.osmos.redux.ecs.systems

import it.unibo.osmos.redux.ecs.entities.{CollidableProperty, Property}
import it.unibo.osmos.redux.utils.MathUtils

case class CollisionSystem(override val priority: Int) extends AbstractSystem[CollidableProperty](priority) {

  //the percentage of mass that an entity can acquire from another during a collision in a tick
  protected val massExchangeRate = 0.02
  //constants that controls how much deceleration is applied to an entity when colliding with another one
  protected val decelerationAmount = 0.08

  override def getGroupProperty: Class[_ <: Property] = classOf[CollidableProperty]

  /**
    * Performs an action on all the entities of the system.
    */
  override def update(): Unit = {

    //get unique entities pairs (lower triangular matrix)
    val uniqueEntities = for {
      (x, xIndex) <- entities.zipWithIndex
      (y, yIndex) <- entities.zipWithIndex
      if xIndex < yIndex
    } yield (x, y)

    uniqueEntities.foreach {
        case (e1, e2) =>
          if (checkCollision(e1, e2)) applyCollisionEffects(e1, e2)
    }
  }

  /**
    * Checks if two entities collide with each other.
    * @param e1 The first entity
    * @param e2 The second entity
    * @return True, if the entities collide; otherwise false
    */
  protected def checkCollision(e1: CollidableProperty, e2: CollidableProperty): Boolean = {
    val dist = MathUtils.distanceBetweenPoints(e1.getPositionComponent.point, e2.getPositionComponent.point)
    dist < (e1.getDimensionComponent.radius + e2.getDimensionComponent.radius)
  }

  /**
    * Applies collision effect to an input entity.
    * @param e1 The first entity
    * @param e2 The second entity
    */
  protected def applyCollisionEffects(e1: CollidableProperty, e2: CollidableProperty): Unit = {
    val (bigEntity, smallEntity) = if (e1.getDimensionComponent.radius > e2.getDimensionComponent.radius) (e1, e2) else (e2, e1)

    //bigger entity should gain size while the other loses it
    exchangeMass(bigEntity, smallEntity)

    //apply deceleration to both entities, proportionally to their size
    val smallDecelerationAmount = (bigEntity.getDimensionComponent.radius / smallEntity.getDimensionComponent.radius) * decelerationAmount
    val bigDecelerationAmount = (smallEntity.getDimensionComponent.radius / bigEntity.getDimensionComponent.radius) * decelerationAmount
    decelerateEntity(smallEntity, smallDecelerationAmount)
    decelerateEntity(bigEntity, bigDecelerationAmount)
  }

  /**
    * Exchanges mass from the small entity to the big one (modify entity radius).
    * @param bigEntity The big entity
    * @param smallEntity The small entity
    */
  protected def exchangeMass(bigEntity: CollidableProperty, smallEntity: CollidableProperty): Unit = {
    val exchangedRadiusValue = smallEntity.getDimensionComponent.radius * massExchangeRate
    val bigRadius = bigEntity.getDimensionComponent.radius
    val tinyRadius = smallEntity.getDimensionComponent.radius

    bigEntity.getDimensionComponent.radius_(bigRadius + exchangedRadiusValue)
    smallEntity.getDimensionComponent.radius_(tinyRadius - exchangedRadiusValue)
  }

  /**
    * Applies deceleration to the input entity.
    * @param entity The entity to slow down
    */
  protected def decelerateEntity(entity: CollidableProperty, percentage: Double): Unit = {
    val accel = entity.getAccelerationComponent

    //gain acceleration even if the entity is still
    if (accel.accelerationX == 0) entity.getAccelerationComponent.accelerationX_(percentage) else {
      entity.getAccelerationComponent.accelerationX_(accel.accelerationX - accel.accelerationX * percentage)
    }
    if (accel.accelerationY == 0) entity.getAccelerationComponent.accelerationY_(percentage) else {
      entity.getAccelerationComponent.accelerationY_(accel.accelerationY - accel.accelerationY * percentage)
    }
  }
}
