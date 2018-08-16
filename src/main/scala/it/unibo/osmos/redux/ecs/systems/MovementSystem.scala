package it.unibo.osmos.redux.ecs.systems

import it.unibo.osmos.redux.ecs.entities.{EntityManager, MovableProperty, Property}
import it.unibo.osmos.redux.mvc.model.MapShape.{Circle, Rectangle}
import it.unibo.osmos.redux.mvc.model._
import it.unibo.osmos.redux.utils.Point

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
    val radius = dimensionComponent.radius
    val speedComponent = entity.getSpeedComponent
    val maxHorizontalPoint = base - radius
    val maxVerticalPoint = height - radius
    val positionComponent = entity.getPositionComponent

    //println("\n" + entity)
    //println("speed " + speedComponent.speedX + " " + speedComponent.speedY)
    //println("position " + positionComponent.point.x + " " + positionComponent.point.y)

    collisionRule match {
      case CollisionRules.bouncing =>
        positionComponent.point match {
          case p if p.x < radius =>
            speedComponent.speedX_(-speedComponent.speedX)
            val newXPosition = radius - (p.x - radius)
            positionComponent.point_(Point(newXPosition, p.y))
          case p if p.x > maxHorizontalPoint =>
            speedComponent.speedX_(-speedComponent.speedX)
            val newXPosition = maxHorizontalPoint - (p.x - maxHorizontalPoint)
            positionComponent.point_(Point(newXPosition, p.y))
          case p if p.y < radius =>
            speedComponent.speedY_(-speedComponent.speedY)
            val newYPosition = radius - (p.y - radius)
            positionComponent.point_(Point(p.x, newYPosition))
          case p if p.y > maxVerticalPoint =>
            speedComponent.speedY_(-speedComponent.speedY)
            val newYPosition = maxVerticalPoint - (p.y - maxVerticalPoint)
            positionComponent.point_(Point(p.x, newYPosition))
          case _ => // no collision, do nothing
        }
      case CollisionRules.instantDeath =>
        positionComponent.point match {
          case p if p.x < radius =>
            dimensionComponent.radius_(radius - (radius - p.x))
            removeEntity(entity)
          case p if p.x > maxHorizontalPoint =>
            dimensionComponent.radius_(radius - (p.x - maxHorizontalPoint))
            removeEntity(entity)
          case p if p.y < radius =>
            dimensionComponent.radius_(radius - (radius - p.y))
            removeEntity(entity)
          case p if p.y > maxVerticalPoint =>
            dimensionComponent.radius_(radius - (p.y - maxVerticalPoint))
            removeEntity(entity)
          case _ => // no collision, do nothing
        }
      case _ => throw new IllegalArgumentException
    }

    //println("speed " + speedComponent.speedX + " " + speedComponent.speedY)
    //println("position " + positionComponent.point.x + " " + positionComponent.point.y)
  }

  private def circleBounce(entity: MovableProperty, radius: Double): Unit = {
    // TODO: implementation
  }

  private def removeEntity(entity: MovableProperty): Unit ={
    // TODO: should be done here?
    if(entity.getDimensionComponent.radius < 0){
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
