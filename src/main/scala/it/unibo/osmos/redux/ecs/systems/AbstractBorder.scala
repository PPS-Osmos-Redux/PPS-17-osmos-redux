package it.unibo.osmos.redux.ecs.systems

import it.unibo.osmos.redux.ecs.components.{SpeedComponent, VectorComponent}
import it.unibo.osmos.redux.ecs.entities.{MovableProperty, Property}
import it.unibo.osmos.redux.mvc.model.CollisionRules
import it.unibo.osmos.redux.utils.{MathUtils, Point}

abstract class AbstractBorder[A <: Property](levelCenter: Point) {

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
            speedComponent.speedX_(-speedComponent.speedX)
            val newXPosition = minHorizontalPoint - (p.x - minHorizontalPoint)
            positionComponent.point_(Point(newXPosition, p.y))
          case p if p.x > maxHorizontalPoint =>
            speedComponent.speedX_(-speedComponent.speedX)
            val newXPosition = maxHorizontalPoint - (p.x - maxHorizontalPoint)
            positionComponent.point_(Point(newXPosition, p.y))
          case _ => // no border collision, do nothing
        }
        positionComponent.point match {
          case p if p.y < minVerticalPoint =>
            speedComponent.speedY_(-speedComponent.speedY)
            val newYPosition = minVerticalPoint - (p.y - minVerticalPoint)
            positionComponent.point_(Point(p.x, newYPosition))
          case p if p.y > maxVerticalPoint =>
            speedComponent.speedY_(-speedComponent.speedY)
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

  private val cellElasticity: Double = 1.0
  private val borderElasticity: Double = 1.0
  private val restitution = cellElasticity * borderElasticity

  override def checkCollision(entity: MovableProperty, collisionRule: CollisionRules.Value): Unit = {
    val speedComponent = entity.getSpeedComponent
    val positionComponent = entity.getPositionComponent
    val currentPosition = positionComponent.point

    //val levelCenter = Point(levelRadius, levelRadius)
    // TODO: possible code repetition, add in MathUtils method to sum point to vector
    // TODO: consider adding data structure that keeps in memory prec position
    val precPosition = Point(currentPosition.x - speedComponent.speedX, currentPosition.y - speedComponent.speedY)
    val maxReachableDistance = levelRadius - entity.getDimensionComponent.radius
    val currentDistanceFromCenter = MathUtils.euclideanDistance(levelCenter, currentPosition)

    if (currentDistanceFromCenter > maxReachableDistance) {
      collisionRule match {
        case CollisionRules.bouncing =>
          // positionComponent.point_(computePositionAfterBounce(currentPosition, precPosition, levelRadius, levelCenter))
          // TODO: probably method name should be refactored to "computeNewPosition"
          // For better understanding see
          // http://gamedev.stackexchange.com/a/29658
          println("levelradius ", levelRadius)
          val newPosition = find_contact_point(levelRadius, entity)
          positionComponent.point_(newPosition)
          // For better understanding see second answer
          // https://stackoverflow.com/questions/573084/bounce-angle
          val newSpeed = computeNewSpeed(positionComponent.point, levelCenter, speedComponent)
          entity.getSpeedComponent.speedX_(newSpeed.speedX)
          entity.getSpeedComponent.speedX_(newSpeed.speedY)
        case CollisionRules.instantDeath =>
        // TODO: implement annihilation case
        case _ => throw new IllegalArgumentException
      }
    }
  }

  private def find_contact_point(levelRadius: Double, entity: MovableProperty): Point = {
    val positionComponent = entity.getPositionComponent
    val A = levelCenter
    val B = Point(positionComponent.point.x - entity.getSpeedComponent.speedX, positionComponent.point.y - entity.getSpeedComponent.speedY)
    val C = positionComponent.point
    val R = levelRadius
    val r = entity.getDimensionComponent.radius

    val AB = VectorComponent(A.x - B.x, A.y - B.y)
    val BC = VectorComponent(B.x - C.x, B.y - C.y)
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
      BD.set_length(BD_len)

      // D
      // B.add(BD)
      Point(B.x + BD.getX, B.y + BD.getY)
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

  private def computeNewSpeed(currentPosition: Point, levelCenter: Point, speedComponent: SpeedComponent): SpeedComponent = {
    val n = currentPosition.subtract(levelCenter).normalized()

    val u = n.multiply(speedComponent.dot(n))
    val w = currentPosition.subtract(u)
    val v_after = w.subtract(u)
    val reflection = v_after.subtract(currentPosition).multiply(restitution)
    SpeedComponent(speedComponent.speedX + reflection.getX, speedComponent.speedY + reflection.getY)
  }
}
