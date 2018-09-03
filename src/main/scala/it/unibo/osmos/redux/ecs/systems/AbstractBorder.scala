package it.unibo.osmos.redux.ecs.systems

import it.unibo.osmos.redux.ecs.components.{DimensionComponent, PositionComponent}
import it.unibo.osmos.redux.ecs.entities.CollidableProperty
import it.unibo.osmos.redux.mvc.model.CollisionRules
import it.unibo.osmos.redux.utils.{MathUtils, Point, Vector}

/** Abstract class implementing the border collision strategy
  *
  * @param levelCenter center of the level
  */
abstract class AbstractBorder(levelCenter: Point, collisionRule: CollisionRules.Value) {

  private val cellElasticity: Double = 1.0
  private val borderElasticity: Double = 1.0
  protected val restitution: Double = cellElasticity * borderElasticity

  /** Checks if an entity has collided with the border.
    * If so, computes it's new position and speed.
    *
    * @param entity the entity to check
    */
  def checkAndSolveCollision(entity: CollidableProperty): Unit

  /** Checks if an entity is outside of the playable field.
    * If so, repositions it inside the playable field.
    *
    * @param entity the entity to check
    */
  def repositionIfOutsideMap(entity: CollidableProperty): Unit
}

/** Implementation of a playing field with rectangular shape */
case class RectangularBorder(levelCenter: Point, collisionRule: CollisionRules.Value, base: Double, height: Double) extends AbstractBorder(levelCenter, collisionRule) {

  override def checkAndSolveCollision(entity: CollidableProperty): Unit = {
    val dimensionComponent = entity.getDimensionComponent
    val entityRadius = dimensionComponent.radius
    val speedComponent = entity.getSpeedComponent
    val minHorizontalPoint = getLowerBoundary(entityRadius, levelCenter.x, base)
    val maxHorizontalPoint = getUpperBoundary(entityRadius, levelCenter.x, base)
    val minVerticalPoint = getLowerBoundary(entityRadius, levelCenter.y, height)
    val maxVerticalPoint = getUpperBoundary(entityRadius, levelCenter.y, height)
    val positionComponent = entity.getPositionComponent
    val position = positionComponent.point

    collisionRule match {
      case CollisionRules.bouncing =>
        val speedVector = speedComponent.vector
        val rx = computeNewSpeedAndPosition(speedVector.x, position.x, minHorizontalPoint, maxHorizontalPoint)
        val ry = computeNewSpeedAndPosition(speedVector.y, position.y, minVerticalPoint, maxVerticalPoint)
        speedComponent.vector_(Vector(rx._1, ry._1))
        positionComponent.point_(Point(rx._2, ry._2))
      case CollisionRules.instantDeath =>
        computeNewRadius(dimensionComponent, position.x, minHorizontalPoint, maxHorizontalPoint)
        computeNewRadius(dimensionComponent, position.y, minVerticalPoint, maxVerticalPoint)
      case _ => throw new IllegalArgumentException
    }
  }

  private def getLowerBoundary(radius: Double, centerCoordinate: Double, borderLength: Double): Double = {
    centerCoordinate - borderLength / 2 + radius
  }

  private def getUpperBoundary(radius: Double, centerCoordinate: Double, borderLength: Double): Double = {
    centerCoordinate + borderLength / 2 - radius
  }

  private def computeNewSpeedAndPosition(speed: Double, position: Double, minReachablePosition: Double, maxReachablePosition: Double): (Double, Double) = {
    position match {
      case p if p < minReachablePosition => (-speed, minReachablePosition - (p - minReachablePosition))
      case p if p > maxReachablePosition => (-speed, maxReachablePosition - (p - maxReachablePosition))
      case _ => (speed, position)
    }
  }

  private def computeNewRadius(dimensionComponent: DimensionComponent, position: Double, minReachablePosition: Double, maxReachablePosition: Double): Unit = {
    val entityRadius = dimensionComponent.radius
    position match {
      case p if p < minReachablePosition => dimensionComponent.radius_(entityRadius - (minReachablePosition - p))
      case p if p > maxReachablePosition => dimensionComponent.radius_(entityRadius - (p - maxReachablePosition))
      case _ => // no border collision, do nothing
    }
  }

  override def repositionIfOutsideMap(entity: CollidableProperty): Unit = {}

}

/** Implementation of a playing field with circular shape */
case class CircularBorder(levelCenter: Point, collisionRule: CollisionRules.Value, levelRadius: Double) extends AbstractBorder(levelCenter, collisionRule) {

  var positionComponent: PositionComponent = _
  var currentPosition: Point = _
  var dimensionComponent: DimensionComponent = _
  var entityRadius: Double = _
  var maxReachableDistance: Double = _
  var currentDistanceFromCenter: Double = _

  /*implicit def pointToVector(point: Point): Vector = {
    Vector(point.x, point.y)
  }*/

  override def checkAndSolveCollision(entity: CollidableProperty): Unit = {
    if (checkCollision(entity)) {
      collisionRule match {
        case CollisionRules.bouncing =>
          val newPosition = computeNewPosition(levelRadius, entity)
          positionComponent.point_(newPosition)

          val speedComponent = entity.getSpeedComponent
          val speedVector = speedComponent.vector
          val newSpeed = computeNewSpeed(positionComponent.point, levelCenter, speedVector)
          speedComponent.vector_(newSpeed)
        case CollisionRules.instantDeath =>
          dimensionComponent.radius_(entityRadius - (currentDistanceFromCenter - maxReachableDistance))
        case _ => throw new IllegalArgumentException
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
    * @param levelRadius
    * @param entity
    * @return
    */
  private def computeNewPosition(levelRadius: Double, entity: CollidableProperty): Point = {
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
    val lengthOfAB = AB.getMagnitude
    val lengthOfBC = BC.getMagnitude

    if (lengthOfBC == 0) {
      C
    } else {
      val b = AB.dot(BC) / Math.pow(lengthOfBC, 2) * -1
      val c = (Math.pow(lengthOfAB, 2) - Math.pow(R - r, 2)) / Math.pow(lengthOfBC, 2)
      val d = b * b - c
      var k = b - Math.sqrt(d)

      if (k < 0) {
        k = b + Math.sqrt(d)
      }

      val BD = C.subtract(B)
      val lengthOfBD = BD.getNewMagnitude(lengthOfBC * k)

      // D
      // B.add(BD)
      Point(B.x + lengthOfBD.x, B.y + lengthOfBD.y)
    }
  }

  /** For better understanding see second answer:
    * https://stackoverflow.com/questions/573084/bounce-angle
    *
    * @param currentPosition entity current position
    * @param levelCenter     level center
    * @return new entity speed
    */
  private def computeNewSpeed(currentPosition: Point, levelCenter: Point, speedVector: Vector): Vector = {
    val v = speedVector
    val n = currentPosition.subtract(levelCenter).normalized()

    val u = n.multiply(v.dot(n))
    val w = v.subtract(u)
    val vAfter = w.subtract(u)
    val reflection = vAfter.subtract(v).multiply(restitution)
    v.add(reflection)
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
}
