package it.unibo.osmos.redux.ecs.systems

import it.unibo.osmos.redux.ecs.entities.EntityType
import it.unibo.osmos.redux.ecs.entities.properties.composed.CollidableProperty
import it.unibo.osmos.redux.ecs.systems.borderconditions.{CircularBorder, RectangularBorder}
import it.unibo.osmos.redux.mvc.controller.levels.structure.Level
import it.unibo.osmos.redux.mvc.controller.levels.structure.MapShape.{Circle, Rectangle}
import it.unibo.osmos.redux.utils.{MathUtils, Point, Vector}

/** System that managing the collision with boundary and between cell */
case class CollisionSystem(levelInfo: Level) extends AbstractSystem[CollidableProperty] {

  //the percentage of mass that an entity can acquire from another during a collision in a tick
  private val MassExchangeRate = 0.38
  //constants that controls how much deceleration is applied to an entity when colliding with another one
  private val DecelerationAmount = 0.01
  //the initial acceleration vector of a steady entity when a collision occurs
  private val StillEntityInitialAcceleration = 0.0035
  //the bouncing rule
  private val bounceRule = levelInfo.levelMap.mapShape match {
    case shape: Rectangle =>
      RectangularBorder(Point(shape.center.x, shape.center.y), levelInfo.levelMap.collisionRule, shape.base, shape.height)
    case shape: Circle =>
      CircularBorder(Point(shape.center.x, shape.center.y), levelInfo.levelMap.collisionRule, shape.radius)
    case _ => throw new IllegalArgumentException("Invalid map shape, unable to initialize bounce rule.")
  }

  override def update(): Unit = {
    //check collision with boundary
    entities foreach (e => bounceRule.checkAndSolveCollision(e))
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

  /** Computes the overlap between two entities.
    *
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

  /** Applies collision effects to two entities that collide with each other.
    *
    * @param e1      The first entity
    * @param e2      The second entity
    * @param overlap The overlap amount
    */
  private def applyCollisionEffects(e1: CollidableProperty, e2: CollidableProperty, overlap: Double): Unit = {
    val (bigEntity, smallEntity) = if (e1.getDimensionComponent.radius > e2.getDimensionComponent.radius) (e1, e2) else (e2, e1)

    //exchange mass between the two entities
    exchangeMass(bigEntity, smallEntity, overlap)

    //compute direction vector
    val dir = MathUtils.unitVector(bigEntity.getPositionComponent.point, smallEntity.getPositionComponent.point)

    //apply deceleration to both entities, proportionally to their size
    accelerateEntity(smallEntity, dir multiply -1)
    accelerateEntity(bigEntity, dir)
  }

  /** Exchanges mass from the small entity to the big one (modify entity radius).
    *
    * @param bigEntity   The big entity
    * @param smallEntity The small entity
    * @param overlap     The overlap amount
    */
  private def exchangeMass(bigEntity: CollidableProperty, smallEntity: CollidableProperty, overlap: Double): Unit = {
    val bigRadius = bigEntity.getDimensionComponent.radius
    val tinyRadius = smallEntity.getDimensionComponent.radius
    //reduce radius of the small entity
    smallEntity.getDimensionComponent.radius_(tinyRadius - overlap * MassExchangeRate)

    //change radius of the big entity and compute the quantity to move the two entity
    val quantityToMove = (bigEntity.getTypeComponent.typeEntity, smallEntity.getTypeComponent.typeEntity) match {
      case (EntityType.AntiMatter, _) | (_, EntityType.AntiMatter) =>
        bigEntity.getDimensionComponent.radius_(bigRadius - overlap * MassExchangeRate)
        (overlap * (1 - MassExchangeRate * 2)) / 2
      case _ =>
        val oldSmallArea = MathUtils.circleArea(tinyRadius)
        val newSmallArea = MathUtils.circleArea(smallEntity.getDimensionComponent.radius)
        val newBigArea = MathUtils.circleArea(bigEntity.getDimensionComponent.radius) + oldSmallArea - newSmallArea
        bigEntity.getDimensionComponent.radius_(MathUtils.areaToRadius(newBigArea))
        limitMaxRadius(bigEntity)
        (overlap - overlap * MassExchangeRate + (bigEntity.getDimensionComponent.radius - bigRadius)) / 2
    }

    moveEntitiesAfterCollision(bigEntity, smallEntity, quantityToMove)
  }

  /** Limit the radius of the entity to the min dimension of the map
    *
    * @param entity the entity
    */
  private def limitMaxRadius(entity: CollidableProperty): Unit = {
    val level = levelInfo.levelMap.mapShape
    val dimension = entity.getDimensionComponent
    level match {
      case map: Rectangle =>
        if(dimension.radius > map.base/2) {
          dimension.radius_(map.base/2)
        }
        if(dimension.radius > map.height/2) {
          dimension.radius_(map.height/2)
        }
      case _ =>
    }
  }

  /** move each entity in the opposite direction to the other of quantity to move, and check collision with boundary
    *
    * @param entity1        first entity
    * @param entity2        second entity
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

  /** Applies acceleration to the input entity.
    *
    * @param entity    The entity.
    * @param direction The vector to apply to the acceleration.
    */
  private def accelerateEntity(entity: CollidableProperty, direction: Vector): Unit = {
    val accel = entity.getAccelerationComponent

    //gain acceleration even if the entity is still
    if (accel.vector == Vector(0, 0)) {
      entity.getAccelerationComponent.vector_(direction multiply StillEntityInitialAcceleration)
    } else {
      accel.vector_(accel.vector add (direction multiply DecelerationAmount))
    }
  }
}
