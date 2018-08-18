package it.unibo.osmos.redux.ecs.systems

import it.unibo.osmos.redux.ecs.components.SpeedComponent
import it.unibo.osmos.redux.ecs.entities.{MovableProperty, Property}
import it.unibo.osmos.redux.mvc.model.CollisionRules
import it.unibo.osmos.redux.utils.{MathUtils, Point}

abstract class AbstractBorder[A <: Property] {

  def checkCollision(entity: A, collisionRule: CollisionRules.Value): Unit
}

case class RectangularBorder(base: Double, height: Double) extends AbstractBorder[MovableProperty] {

  override def checkCollision(entity: MovableProperty, collisionRule: CollisionRules.Value): Unit = {
    val dimensionComponent = entity.getDimensionComponent
    val entityRadius = dimensionComponent.radius
    val speedComponent = entity.getSpeedComponent
    val maxHorizontalPoint = base - entityRadius
    val maxVerticalPoint = height - entityRadius
    val positionComponent = entity.getPositionComponent

    collisionRule match {
      case CollisionRules.bouncing =>
        positionComponent.point match {
          case p if p.x < entityRadius =>
            speedComponent.speedX_(-speedComponent.speedX)
            val newXPosition = entityRadius - (p.x - entityRadius)
            positionComponent.point_(Point(newXPosition, p.y))
          case p if p.x > maxHorizontalPoint =>
            speedComponent.speedX_(-speedComponent.speedX)
            val newXPosition = maxHorizontalPoint - (p.x - maxHorizontalPoint)
            positionComponent.point_(Point(newXPosition, p.y))
          case _ => // no border collision, do nothing
        }
        positionComponent.point match {
          case p if p.y < entityRadius =>
            speedComponent.speedY_(-speedComponent.speedY)
            val newYPosition = entityRadius - (p.y - entityRadius)
            positionComponent.point_(Point(p.x, newYPosition))
          case p if p.y > maxVerticalPoint =>
            speedComponent.speedY_(-speedComponent.speedY)
            val newYPosition = maxVerticalPoint - (p.y - maxVerticalPoint)
            positionComponent.point_(Point(p.x, newYPosition))
          case _ => // no border collision, do nothing
        }
      case CollisionRules.instantDeath =>
        positionComponent.point match {
          case p if p.x < entityRadius =>
            dimensionComponent.radius_(entityRadius - (entityRadius - p.x))
          case p if p.x > maxHorizontalPoint =>
            dimensionComponent.radius_(entityRadius - (p.x - maxHorizontalPoint))
          case _ => // no border collision, do nothing
        }
        positionComponent.point match {
          case p if p.y < entityRadius =>
            dimensionComponent.radius_(entityRadius - (entityRadius - p.y))
          case p if p.y > maxVerticalPoint =>
            dimensionComponent.radius_(entityRadius - (p.y - maxVerticalPoint))
          case _ => // no border collision, do nothing
        }
      case _ => throw new IllegalArgumentException
    }
  }
}

case class CircularBorder(levelRadius: Double) extends AbstractBorder[MovableProperty] {

  override def checkCollision(entity: MovableProperty, collisionRule: CollisionRules.Value): Unit = {
    val speedComponent = entity.getSpeedComponent
    val positionComponent = entity.getPositionComponent
    val currentPosition = positionComponent.point

    val levelCenter = Point(levelRadius, levelRadius)
    // TODO: possible code repetition, add in MathUtils method to sum point to vector
    // TODO: consider adding data structure that keeps in memory prec position
    val precPosition = Point(currentPosition.x - speedComponent.speedX, currentPosition.y - speedComponent.speedY)
    val maxReachableDistance = levelRadius - entity.getDimensionComponent.radius
    val currentDistanceFromCenter = MathUtils.euclideanDistance(levelCenter, currentPosition)

    if (currentDistanceFromCenter > maxReachableDistance) {
      collisionRule match {
        case CollisionRules.bouncing =>
          positionComponent.point_(computePositionAfterBounce(currentPosition, precPosition, levelRadius, levelCenter))
          // TODO: test correctness
          // http://stackoverflow.com/questions/573084/bounce-angle
          val n = SpeedComponent(currentPosition.x - levelCenter.x, currentPosition.y - levelCenter.y)
          val n_len = Math.sqrt(Math.pow(n.speedX, 2) + Math.pow(n.speedY, 2))
          val n_normalized = SpeedComponent(n.speedX / n_len, n.speedY / n_len)
          val dot = speedComponent.speedX * n_normalized.speedX + speedComponent.speedY * n_normalized.speedY
          val u = SpeedComponent(n_normalized.speedX * dot, n_normalized.speedY * dot)
          val w = SpeedComponent(speedComponent.speedX - u.speedX, speedComponent.speedY - u.speedY)
          val v_after = SpeedComponent(w.speedX - u.speedX, w.speedY - u.speedY)
          val reflection = SpeedComponent(v_after.speedX - speedComponent.speedX, v_after.speedY - speedComponent.speedY)
          speedComponent.speedX_(speedComponent.speedX + reflection.speedX)
          speedComponent.speedY_(speedComponent.speedY + reflection.speedY)
        case CollisionRules.instantDeath =>
        // TODO: implement annihilation case
        case _ => throw new IllegalArgumentException
      }
    }
  }

  private def computePositionAfterBounce(currentPosition: Point, precPosition: Point, levelRadius: Double, levelCenter: Point): Point = {
    val straightLine = GeometricalStraightLine(currentPosition, precPosition)
    val circumference = GeometricalCircumference(levelCenter, levelRadius)

    // computing intersection between straight line and circumference
    val eq_a = 1 + Math.pow(straightLine.m, 2)
    val eq_b = 2 * straightLine.m * straightLine.q + circumference.a + circumference.b * straightLine.m
    val eq_c = Math.pow(straightLine.q, 2) + circumference.b * straightLine.q + circumference.c
    val delta = Math.pow(eq_b, 2) - 4 * eq_a * eq_c

    // x1,x2 = (-b ± sqrt(Δ) / 2 * a)
    val x_1 = (-eq_b + Math.sqrt(delta)) / 2 * eq_a
    val y_1 = straightLine.m * x_1 + straightLine.q
    val p_1 = Point(x_1, y_1)

    var tangentToCircumference: GeometricalStraightLine = null

    MathUtils.isPointBetweenPoints(p_1, precPosition, currentPosition) match {
      case true =>
        val straightLineFromCenterToP_1 = GeometricalStraightLine(p_1, levelCenter)
        val tang_m = -1 / straightLineFromCenterToP_1.m
        val tang_q = straightLineFromCenterToP_1.q
        tangentToCircumference = GeometricalStraightLine(tang_m, tang_q)
      case false =>
        // must compute second result
        val x_2 = (-eq_b - Math.sqrt(delta)) / 2 * eq_a
        val y_2 = straightLine.m * x_2 + straightLine.q
        val p_2 = Point(x_2, y_2)

        val straightLineFromCenterToP_2 = GeometricalStraightLine(p_2, levelCenter)
        val tang_m = -1 / straightLineFromCenterToP_2.m
        val tang_q = straightLineFromCenterToP_2.q
        tangentToCircumference = GeometricalStraightLine(tang_m, tang_q)
    }

    val perpendicular_m = -1 / tangentToCircumference.m
    val perpendicular_q = 1 / tangentToCircumference.m * currentPosition.x + currentPosition.y
    val perpendicularOfTangent = GeometricalStraightLine(perpendicular_m, perpendicular_q)

    val midPointX = (perpendicularOfTangent.q - tangentToCircumference.q) / (perpendicularOfTangent.m - tangentToCircumference.m)
    val midPointY = tangentToCircumference.m * midPointX + tangentToCircumference.q

    Point(2 * midPointX - currentPosition.x, 2 * midPointY - currentPosition.y)
  }

}
