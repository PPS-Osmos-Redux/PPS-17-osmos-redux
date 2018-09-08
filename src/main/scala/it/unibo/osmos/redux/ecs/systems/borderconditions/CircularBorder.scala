package it.unibo.osmos.redux.ecs.systems.borderconditions

import it.unibo.osmos.redux.ecs.components.{DimensionComponent, PositionComponent}
import it.unibo.osmos.redux.ecs.entities.properties.composed.CollidableProperty
import it.unibo.osmos.redux.mvc.controller.levels.structure.CollisionRules
import it.unibo.osmos.redux.utils.{MathUtils, Point, Vector}

/** Collision implementation for a playing field with circular shape */
case class CircularBorder(levelCenter: Point, collisionRule: CollisionRules.Value, levelRadius: Double) extends AbstractBorder(levelCenter, collisionRule) {

  private var positionComponent: PositionComponent = _
  private var currentPosition: Point = _
  private var dimensionComponent: DimensionComponent = _
  private var entityRadius: Double = _
  private var maxReachableDistance: Double = _
  private var currentDistanceFromCenter: Double = _

  override def checkAndSolveCollision(entity: CollidableProperty): Unit = {
    if (checkCollision(entity)) {
      collisionRule match {
        case CollisionRules.bouncing =>
          val newPosition = computeNewPosition(entity)
          positionComponent.point_(newPosition)

          val speedComponent = entity.getSpeedComponent
          val speedVector = speedComponent.vector
          val newSpeed = computeNewSpeed(positionComponent.point, speedVector)
          speedComponent.vector_(newSpeed)
        case CollisionRules.instantDeath =>
          dimensionComponent.radius_(entityRadius - (currentDistanceFromCenter - maxReachableDistance))
        case _ => throw new IllegalArgumentException
      }
    }
  }


  override def repositionIfOutsideMap(entity: CollidableProperty): Unit = {
    if (checkCollision(entity)) {
      collisionRule match {
        case CollisionRules.bouncing =>
          val vector = MathUtils.unitVector(levelCenter, currentPosition)
          val back = currentDistanceFromCenter - levelRadius + entityRadius
          positionComponent.point_(currentPosition.add(vector.multiply(back)))
        case _ =>
      }
    }
  }

  private def checkCollision(entity: CollidableProperty): Boolean = {
    positionComponent = entity.getPositionComponent
    currentPosition = positionComponent.point
    dimensionComponent = entity.getDimensionComponent
    entityRadius = dimensionComponent.radius
    maxReachableDistance = levelRadius - entityRadius
    currentDistanceFromCenter = MathUtils.euclideanDistance(levelCenter, currentPosition)
    currentDistanceFromCenter > maxReachableDistance
  }

  /** For better understanding see: http://gamedev.stackexchange.com/a/29658
    *
    * @param entity the entity to compute new position after bounce
    * @return the entity correct position
    */
  private def computeNewPosition(entity: CollidableProperty): Point = {
    val entityPosition = entity.getPositionComponent.point
    val entitySpeed = entity.getSpeedComponent.vector
    val A = levelCenter
    // TODO: consider keep in memory prec position to avoid its recomputation
    val B = entityPosition.subtract(entitySpeed)
    val C = entityPosition
    val R = levelRadius
    val r = entity.getDimensionComponent.radius

    val AB = A.subtract(B)
    val BC = B.subtract(C)
    val magnitudeOfAB = AB.getMagnitude
    val magnitudeOfBC = BC.getMagnitude

    if (magnitudeOfBC == 0) {
      C
    } else {
      val b = AB.dot(BC) / Math.pow(magnitudeOfBC, 2) * -1
      val c = (Math.pow(magnitudeOfAB, 2) - Math.pow(R - r, 2)) / Math.pow(magnitudeOfBC, 2)
      val d = b * b - c
      var k = b - Math.sqrt(d)

      if (k < 0) {
        k = b + Math.sqrt(d)
      }

      val BD = C.subtract(B)
      val newMagnitudeOfBD = BD.getNewMagnitude(magnitudeOfBC * k)

      // D
      B.add(newMagnitudeOfBD)
      //Point(B.x + newBDMagnitude.x, B.y + newBDMagnitude.y)
    }
  }

  /** For better understanding see second answer:
    * https://stackoverflow.com/questions/573084/bounce-angle
    *
    * @param currentPosition the entity position computed at computeNewPosition()
    * @return the new entity speed after bounce
    */
  private def computeNewSpeed(currentPosition: Point, speedVector: Vector): Vector = {
    val v = speedVector
    val n = currentPosition.subtract(levelCenter).normalized()

    val u = n.multiply(v.dot(n))
    val w = v.subtract(u)
    val vAfter = w.subtract(u)
    val reflection = vAfter.subtract(v).multiply(restitution)
    v.add(reflection)
  }
}
