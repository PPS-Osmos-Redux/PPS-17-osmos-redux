package it.unibo.osmos.redux.ecs.systems

import it.unibo.osmos.redux.ecs.components.EntityType
import it.unibo.osmos.redux.ecs.entities.CollidableProperty
import it.unibo.osmos.redux.utils.MathUtils

case class CollisionSystem() extends AbstractSystem[CollidableProperty] {

  //constants that controls how much deceleration is applied to an entity when colliding with another one
  private val decelerationAmount = 0.1
  //constant that define the initial acceleration of a steady entity when a collision occurs
  private val initialAcceleration = 0.001

  override def getGroupProperty: Class[CollidableProperty] = classOf[CollidableProperty]

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
    val tinyRadius = (e1.getDimensionComponent.radius, e2.getDimensionComponent.radius) match {
      case (r1, r2) if r1 > r2 => r2
      case (r1, _) => r1
    }
    val maxDist = MathUtils.euclideanDistance(e1.getPositionComponent.point, e2.getPositionComponent.point)
    val currDist = e1.getDimensionComponent.radius + e2.getDimensionComponent.radius
    currDist - maxDist match {
      case overlap if overlap <= 0 => 0
      case overlap if overlap > tinyRadius => tinyRadius
      case overlap => overlap
    }
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
    val bigRadius = bigEntity.getDimensionComponent.radius
    val tinyRadius = smallEntity.getDimensionComponent.radius

    (bigEntity.getTypeComponent.typeEntity, smallEntity.getTypeComponent.typeEntity) match {
      case (EntityType.AntiMatter, _) | (_, EntityType.AntiMatter) =>
        bigEntity.getDimensionComponent.radius_(bigRadius - (overlap/2))
        smallEntity.getDimensionComponent.radius_(tinyRadius - (overlap/2))
      case _ =>
        smallEntity.getDimensionComponent.radius_(tinyRadius - overlap)
        bigEntity.getDimensionComponent.radius_(bigRadius + overlap)
        //move the big entity
        val bigEntityPosition = bigEntity.getPositionComponent
        val bigUnitVector = MathUtils.unitVector(bigEntityPosition.point, smallEntity.getPositionComponent.point)
        bigEntityPosition.point_(bigEntityPosition.point add (bigUnitVector multiply (overlap/2)))
        //move the small entity
        val smallEntityPosition = smallEntity.getPositionComponent
        val smallUnitVector = MathUtils.unitVector(smallEntityPosition.point, bigEntity.getPositionComponent.point)
        smallEntityPosition.point_(smallEntityPosition.point add (smallUnitVector multiply (overlap/2)))
    }
  }


  /**
    * Applies deceleration to the input entity.
    * @param entity The entity to slow down
    * @param percentage The percentage of deceleration to apply
    */
  private def decelerateEntity(entity: CollidableProperty, percentage: Double): Unit = {
    val accel = entity.getAccelerationComponent

    //gain acceleration even if the entity is still
    if (accel.vector.x == 0) {
      entity.getAccelerationComponent.vector.x_(initialAcceleration * percentage)
      entity.getAccelerationComponent.vector.y_(initialAcceleration * percentage)
    } else {
      entity.getAccelerationComponent.vector.x_(accel.vector.x - accel.vector.x * percentage)
      entity.getAccelerationComponent.vector.y_(accel.vector.y - accel.vector.y * percentage)
    }
  }
}
