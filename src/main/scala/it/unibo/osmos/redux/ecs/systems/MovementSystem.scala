package it.unibo.osmos.redux.ecs.systems

import it.unibo.osmos.redux.ecs.entities.{MovableProperty, Property}
import it.unibo.osmos.redux.mvc.model.MapShape.{Circle, Rectangle}
import it.unibo.osmos.redux.mvc.model._
import it.unibo.osmos.redux.utils.Point

case class MovementSystem(levelInfo: Level) extends AbstractSystem[MovableProperty] {

  private val bounceRule = levelInfo.levelMap.mapShape match {
    case shape: Rectangle => RectangularBorder(shape.base, shape.height)
    case shape: Circle => CircularBorder(shape.radius)
    case _ => throw new IllegalArgumentException
  }
  private val collisionRule = levelInfo.levelMap.collisionRule

  override def getGroupProperty: Class[_ <: Property] = classOf[MovableProperty]

  override def update(): Unit = {
    entities foreach (entity => {
      updateEntitySpeed(entity)
      updateEntityPosition(entity)
      bounceRule.checkCollision(entity, collisionRule)
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

