package it.unibo.osmos.redux.ecs.systems

import it.unibo.osmos.redux.ecs.components.{SpeedComponent, VectorComponent}
import it.unibo.osmos.redux.ecs.entities.{MovableProperty, Property}
import it.unibo.osmos.redux.mvc.model.CollisionRules
import it.unibo.osmos.redux.utils.{MathUtils, Point, Vector}

abstract class AbstractBorder[A <: Property](levelCenter: Point) {

  private val cellElasticity: Double = 1.0
  private val borderElasticity: Double = 1.0
  protected val restitution: Double = cellElasticity * borderElasticity
  
  def checkCollision(entity: A, collisionRule: CollisionRules.Value): Unit
}

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

case class CircularBorder(levelCenter: Point, levelRadius: Double) extends AbstractBorder[MovableProperty](levelCenter) {

  override def checkCollision(entity: MovableProperty, collisionRule: CollisionRules.Value): Unit = {
    val speedComponent = entity.getSpeedComponent
    val positionComponent = entity.getPositionComponent
    val currentPosition = positionComponent.point

    // TODO: consider adding data structure that keeps in memory prec position
    val precPosition = Point(currentPosition.x - speedComponent.vector.x, currentPosition.y - speedComponent.vector.y)
    val maxReachableDistance = levelRadius - entity.getDimensionComponent.radius
    val currentDistanceFromCenter = MathUtils.euclideanDistance(levelCenter, currentPosition)

    if (currentDistanceFromCenter > maxReachableDistance) {
      collisionRule match {
        case CollisionRules.bouncing =>
          // positionComponent.point_(computePositionAfterBounce(currentPosition, precPosition, levelRadius, levelCenter))
          // TODO: probably method name should be refactored to "computeNewPosition"
          // For better understanding see
          // http://gamedev.stackexchange.com/a/29658
          val newPosition = find_contact_point(levelRadius, entity)
          positionComponent.point_(newPosition)
          // For better understanding see second answer
          // https://stackoverflow.com/questions/573084/bounce-angle
          val newSpeed = computeNewSpeed(positionComponent.point, levelCenter, speedComponent)
          entity.getSpeedComponent.vector.x_(newSpeed.x)
          entity.getSpeedComponent.vector.y_(newSpeed.y)
        case CollisionRules.instantDeath =>
        // TODO: implement annihilation case
        case _ => throw new IllegalArgumentException
      }
    }
  }

  private def find_contact_point(levelRadius: Double, entity: MovableProperty): Point = {
    val positionComponent = entity.getPositionComponent
    val A = levelCenter
    val B = Point(positionComponent.point.x - entity.getSpeedComponent.vector.x, positionComponent.point.y - entity.getSpeedComponent.vector.y)
    val C = positionComponent.point
    val R = levelRadius
    val r = entity.getDimensionComponent.radius

    val AB = Vector(A.x - B.x, A.y - B.y)
    val BC = Vector(B.x - C.x, B.y - C.y)
    val AB_len = AB.get_length
    val BC_len = BC.get_length

    if (BC_len == 0) {
      C
    } else {
      val b = AB.dot(BC) / Math.pow(BC_len, 2) * -1
      val c = (Math.pow(AB_len, 2) - Math.pow(R - r, 2)) / Math.pow(BC_len, 2)
      val d = b * b - c
      var k = b - Math.sqrt(d)

      if (k < 0) {
        k = b + Math.sqrt(d)
      }

      val BD = C.subtract(B)
      val BD_len = BC_len * k
      val BD_length = BD.set_length(BD_len)

      // D
      // B.add(BD)
      Point(B.x + BD_length.x, B.y + BD_length.y)
    }
  }

  private def computeNewSpeed(currentPosition: Point, levelCenter: Point, speedComponent: SpeedComponent): Vector = {
    val world_pt = levelCenter
    val ball_pt = currentPosition
    val v = speedComponent.vector
    val n = ball_pt.subtract(world_pt).normalized()

    val u = n.multiply(v.dot(n))
    val w = v.subtract(u)
    val v_after = w.subtract(u)
    val reflection = v_after.subtract(v).multiply(restitution)
    Vector(speedComponent.vector.x + reflection.x, speedComponent.vector.y + reflection.y)
  }
}
