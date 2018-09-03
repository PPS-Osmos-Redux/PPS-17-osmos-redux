package it.unibo.osmos.redux.ecs.systems

import it.unibo.osmos.redux.ecs.entities.{CollidableProperty, EntityType}
import it.unibo.osmos.redux.ecs.systems.borderconditions.{CircularBorder, RectangularBorder}
import it.unibo.osmos.redux.mvc.model.Level
import it.unibo.osmos.redux.mvc.model.MapShape.{Circle, Rectangle}
import it.unibo.osmos.redux.utils.{MathUtils, Point, Vector}

case class CollisionSystem(levelInfo: Level) extends AbstractSystem[CollidableProperty] {

  //the percentage of mass that an entity can acquire from another during a collision in a tick
  private val massExchangeRate = 0.2
  //constants that controls how much deceleration is applied to an entity when colliding with another one
  private val decelerationAmount = 0.1
  //constant that define the initial acceleration of a steady entity when a collision occurs
  private val initialAcceleration = 0.001

  private val initialAccelerationVector = Vector(initialAcceleration, initialAcceleration)

  private val collisionRule = levelInfo.levelMap.collisionRule
  private val bounceRule = levelInfo.levelMap.mapShape match {
    case shape: Rectangle => RectangularBorder(Point(shape.center._1, shape.center._2), collisionRule, shape.base, shape.height)
    case shape: Circle => CircularBorder(Point(shape.center._1, shape.center._2), collisionRule, shape.radius)
    case _ => throw new IllegalArgumentException
  }

  override def update(): Unit = {
    //check collision with boundary
    entities foreach(e => bounceRule.checkAndSolveCollision(e))
    //check collision other entities
    for {
      (e1, xIndex) <- entities.zipWithIndex
      (e2, yIndex) <- entities.zipWithIndex
      if xIndex < yIndex //skip useless double checks
      if e1.getCollidableComponent.isCollidable && e2.getCollidableComponent.isCollidable
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
    //reduce radius of the small entity
    smallEntity.getDimensionComponent.radius_(tinyRadius - overlap*massExchangeRate)

    //change radius of the big entity and compute the quantity to move the two entity
    val quantityToMove = (bigEntity.getTypeComponent.typeEntity, smallEntity.getTypeComponent.typeEntity) match {
      case (EntityType.AntiMatter, _) | (_, EntityType.AntiMatter) =>
        bigEntity.getDimensionComponent.radius_(bigRadius - overlap*massExchangeRate)
        (overlap * (1 - massExchangeRate*2)) / 2
      case _ =>
        bigEntity.getDimensionComponent.radius_(bigRadius + overlap*massExchangeRate)
        overlap/2
    }

    moveEntitiesAfterCollision(bigEntity, smallEntity, quantityToMove)
  }

  /**
    * move each entity in the opposite direction to the other of quantity to move, and check collision with boundary
    * @param entity1 first entity
    * @param entity2 second entity
    * @param quantityToMove shift of each entity
    */
  private def moveEntitiesAfterCollision(entity1: CollidableProperty, entity2: CollidableProperty, quantityToMove: Double): Unit = {
    val position1 = entity1.getPositionComponent
    val position2 = entity2.getPositionComponent
    val unitVector = MathUtils.unitVector(position1.point, position2.point)
    position1.point_(position1.point add (unitVector multiply quantityToMove))
    position2.point_(position2.point add (unitVector multiply (-quantityToMove)))
    bounceRule.repositionIfOutsideMap(entity1)
    bounceRule.repositionIfOutsideMap(entity2)
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
      entity.getAccelerationComponent.vector_(initialAccelerationVector.multiply(percentage))
    } else {
      val temp = accel.vector.multiply(percentage)
      entity.getAccelerationComponent.vector_(accel.vector.subtract(temp))
      //entity.getAccelerationComponent.vector.x_(accel.vector.x - accel.vector.x * percentage)
      //entity.getAccelerationComponent.vector.y_(accel.vector.y - accel.vector.y * percentage)
    }
  }
}
