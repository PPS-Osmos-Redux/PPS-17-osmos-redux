package it.unibo.osmos.redux.ecs.systems

import it.unibo.osmos.redux.ecs.entities.{MovableProperty, Property}
import it.unibo.osmos.redux.mvc.model.CollisionRules
import it.unibo.osmos.redux.utils.{MathUtils, Point, Vector}

/** Abstract class implementing the border collision strategy
  *
  * @param levelCenter center of the level
  * @tparam
  */
abstract class AbstractBorder[A <: Property](levelCenter: Point) {

  private val cellElasticity: Double = 1.0
  private val borderElasticity: Double = 1.0
  protected val restitution: Double = cellElasticity * borderElasticity

  /** Checks if an entity has collided with the border.
    * If so, computes it's new position and speed
    *
    * @param entity
    * @param collisionRule
    */
  def checkCollision(entity: A, collisionRule: CollisionRules.Value): Unit
}

/** Implementation of a playing field with rectangular shape */
case class RectangularBorder(levelCenter: Point, base: Double, height: Double) extends AbstractBorder[MovableProperty](levelCenter) {

  override def checkCollision(entity: MovableProperty, collisionRule: CollisionRules.Value): Unit = {
    val dimensionComponent = entity.getDimensionComponent
    val entityRadius = dimensionComponent.radius
    val speedComponent = entity.getSpeedComponent
    val minHorizontalPoint = levelCenter.x - base / 2 + entityRadius
    val minVerticalPoint = levelCenter.y - height / 2 + entityRadius
    val maxHorizontalPoint = levelCenter.x + base / 2 - entityRadius
    val maxVerticalPoint = levelCenter.y + height / 2 - entityRadius
    val positionComponent = entity.getPositionComponent

    collisionRule match {
      case CollisionRules.bouncing =>
        positionComponent.point match {
          case p if p.x < minHorizontalPoint =>
            speedComponent.vector.x_(-speedComponent.vector.x)
            val newXPosition = minHorizontalPoint - (p.x - minHorizontalPoint)
            positionComponent.point_(Point(newXPosition, p.y))
          case p if p.x > maxHorizontalPoint =>
            speedComponent.vector.x_(-speedComponent.vector.x)
            val newXPosition = maxHorizontalPoint - (p.x - maxHorizontalPoint)
            positionComponent.point_(Point(newXPosition, p.y))
          case _ => // no border collision, do nothing
        }
        positionComponent.point match {
          case p if p.y < minVerticalPoint =>
            speedComponent.vector.y_(-speedComponent.vector.y)
            val newYPosition = minVerticalPoint - (p.y - minVerticalPoint)
            positionComponent.point_(Point(p.x, newYPosition))
          case p if p.y > maxVerticalPoint =>
            speedComponent.vector.y_(-speedComponent.vector.y)
            val newYPosition = maxVerticalPoint - (p.y - maxVerticalPoint)
            positionComponent.point_(Point(p.x, newYPosition))
          case _ => // no border collision, do nothing
        }
      case CollisionRules.instantDeath =>
        positionComponent.point match {
          case p if p.x < minHorizontalPoint =>
            dimensionComponent.radius_(entityRadius - (minHorizontalPoint - p.x))
          case p if p.x > maxHorizontalPoint =>
            dimensionComponent.radius_(entityRadius - (p.x - maxHorizontalPoint))
          case _ => // no border collision, do nothing
        }
        positionComponent.point match {
          case p if p.y < minVerticalPoint =>
            dimensionComponent.radius_(entityRadius - (minVerticalPoint - p.y))
          case p if p.y > maxVerticalPoint =>
            dimensionComponent.radius_(entityRadius - (p.y - maxVerticalPoint))
          case _ => // no border collision, do nothing
        }
      case _ => throw new IllegalArgumentException
    }
  }
}

/** Implementation of a playing field with circular shape */
case class CircularBorder(levelCenter: Point, levelRadius: Double) extends AbstractBorder[MovableProperty](levelCenter) {

  override def checkCollision(entity: MovableProperty, collisionRule: CollisionRules.Value): Unit = {
    val positionComponent = entity.getPositionComponent
    val currentPosition = positionComponent.point
    val dimensionComponent = entity.getDimensionComponent
    val entityRadius = dimensionComponent.radius
    val maxReachableDistance = levelRadius - entityRadius
    val currentDistanceFromCenter = MathUtils.euclideanDistance(levelCenter, currentPosition)

    if (currentDistanceFromCenter > maxReachableDistance) {
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

  /** For better understanding see: http://gamedev.stackexchange.com/a/29658
    *
    * @param levelRadius
    * @param entity
    * @return
    */
  private def computeNewPosition(levelRadius: Double, entity: MovableProperty): Point = {
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
    val lengthOfAB = AB.getLength
    val lengthOfBC = BC.getLength

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
      val lengthOfBD = BD.getNewLength(lengthOfBC * k)

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
    * @param speedComponent  entity speed component
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
}
