package it.unibo.osmos.redux.ecs.systems

import it.unibo.osmos.redux.ecs.entities.{EntityManager, MovableProperty, Property}
import it.unibo.osmos.redux.mvc.model.MapShape.{Circle, Rectangle}
import it.unibo.osmos.redux.mvc.model._
import it.unibo.osmos.redux.utils.{MathUtils, Point}

case class MovementSystem(override val priority: Int, levelInfo: Level) extends AbstractSystem[MovableProperty](priority) {

  private val mapShape = levelInfo.levelMap.mapShape
  private val collisionRule = levelInfo.levelMap.collisionRule

  override def getGroupProperty: Class[_ <: Property] = classOf[MovableProperty]

  override def update(): Unit = {
    entities foreach (entity => {
      updateEntitySpeed(entity)
      updateEntityPosition(entity)
      borderBounce(entity)
    })
  }

  private def updateEntitySpeed(entity: MovableProperty): Unit = {
    val accelerationComponent = entity.getAccelerationComponent
    val speedComponent = entity.getSpeedComponent
    speedComponent.speedX_(speedComponent.speedX + accelerationComponent.accelerationX)
    speedComponent.speedY_(speedComponent.speedY + accelerationComponent.accelerationY)
    accelerationComponent.accelerationX_(0.0)
    accelerationComponent.accelerationY_(0.0)
  }

  private def borderBounce(entity: MovableProperty): Unit = {
    mapShape match {
      case shape: Rectangle => rectangleBounce(entity, shape.base, shape.height)
      case shape: Circle => circleBounce(entity, shape.radius)
      case _ => throw new IllegalArgumentException
    }
  }

  private def rectangleBounce(entity: MovableProperty, base: Double, height: Double): Unit = {
    val dimensionComponent = entity.getDimensionComponent
    val entityRadius = dimensionComponent.radius
    val speedComponent = entity.getSpeedComponent
    val maxHorizontalPoint = base - entityRadius
    val maxVerticalPoint = height - entityRadius
    val positionComponent = entity.getPositionComponent

    //println("\n" + entity)
    //println("speed " + speedComponent.speedX + " " + speedComponent.speedY)
    //println("position " + positionComponent.point.x + " " + positionComponent.point.y)

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
          case _ => // no collision, do nothing
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
          case _ => // no collision, do nothing
        }
      case CollisionRules.instantDeath =>
        positionComponent.point match {
          case p if p.x < entityRadius =>
            dimensionComponent.radius_(entityRadius - (entityRadius - p.x))
            removeEntity(entity)
          case p if p.x > maxHorizontalPoint =>
            dimensionComponent.radius_(entityRadius - (p.x - maxHorizontalPoint))
            removeEntity(entity)
          case _ => // no collision, do nothing
        }
        positionComponent.point match {
          case p if p.y < entityRadius =>
            dimensionComponent.radius_(entityRadius - (entityRadius - p.y))
            removeEntity(entity)
          case p if p.y > maxVerticalPoint =>
            dimensionComponent.radius_(entityRadius - (p.y - maxVerticalPoint))
            removeEntity(entity)
          case _ => // no collision, do nothing
        }
      case _ => throw new IllegalArgumentException
    }

    //println("speed " + speedComponent.speedX + " " + speedComponent.speedY)
    //println("position " + positionComponent.point.x + " " + positionComponent.point.y)
  }

  private def circleBounce(entity: MovableProperty, levelRadius: Double): Unit = {
    val speedComponent = entity.getSpeedComponent
    val positionComponent = entity.getPositionComponent
    val currentPosition = positionComponent.point

    val levelCenter = Point(levelRadius, levelRadius)
    // TODO: possible code repetition, add in MathUtils method to sum point to vector
    // TODO: consider adding data structure that keeps in memory prec position
    val precPosition = Point(currentPosition.x - speedComponent.speedX, currentPosition.y - speedComponent.speedY)
    val maxReachableDistance = levelRadius - entity.getDimensionComponent.radius
    val currentDistanceFromCenter = MathUtils.distanceBetweenPoints(levelCenter, currentPosition)

    if (currentDistanceFromCenter > maxReachableDistance) {
      collisionRule match {
        case CollisionRules.bouncing =>
          positionComponent.point_(computePositionAfterBounce(currentPosition, precPosition, levelRadius, levelCenter))
          // TODO: compute new speedX and speedY
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

  private def removeEntity(entity: MovableProperty): Unit = {
    // TODO: should be done here?
    if (entity.getDimensionComponent.radius < 0) {
      EntityManager.delete(entity)
    }
  }

  private def updateEntityPosition(entity: MovableProperty): Unit = {
    val positionComponent = entity.getPositionComponent
    val speedComponent = entity.getSpeedComponent
    val updatedXPosition = positionComponent.point.x + speedComponent.speedX
    val updatedYPosition = positionComponent.point.y + speedComponent.speedY
    entity.getPositionComponent.point_(Point(updatedXPosition, updatedYPosition))
  }
}

trait GeometricalStraightLine {
  val m: Double
  val q: Double
}

object GeometricalStraightLine {
  def apply(p1: Point, p2: Point): GeometricalStraightLine = {
    // computes straight line between two points
    val m = (p2.y - p1.y) / (p2.x - p1.x)
    val q = p1.y - p1.x * (p2.y - p1.y) / (p2.x - p1.x)
    GeometricalStraightLineImpl(m, q)
  }

  def apply(m: Double, q: Double): GeometricalStraightLine = GeometricalStraightLineImpl(m, q)

  private case class GeometricalStraightLineImpl(override val m: Double, override val q: Double) extends GeometricalStraightLine {
    // val m: Double = (p2.y - p1.y) / (p2.x - p1.x)
    // val q: Double = p1.y - p1.x * (p2.y - p1.y) / (p2.x - p1.x)
  }

}

trait GeometricalCircumference {
  val a: Double
  val b: Double
  val c: Double
}

object GeometricalCircumference {
  def apply(center: Point, radius: Double): GeometricalCircumference = {
    // computes circumference given center and radius
    val a: Double = 2 * center.x
    val b: Double = -2 * center.y
    val c: Double = Math.pow(-a / 2, 2) + Math.pow(-b / 2, 2) - Math.pow(radius, 2)
    GeometricalCircumferenceImpl(a, b, c)
  }

  case class GeometricalCircumferenceImpl(override val a: Double, override val b: Double, override val c: Double) extends GeometricalCircumference {
    //val a: Double = 2 * center.x
    //val b: Double = -2 * center.y
    //val c: Double = Math.pow(-a / 2, 2) + Math.pow(-b / 2, 2) - Math.pow(radius, 2)
  }

}

