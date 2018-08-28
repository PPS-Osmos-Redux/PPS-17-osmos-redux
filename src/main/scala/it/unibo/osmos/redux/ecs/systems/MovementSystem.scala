package it.unibo.osmos.redux.ecs.systems

import it.unibo.osmos.redux.ecs.entities.MovableProperty
import it.unibo.osmos.redux.mvc.model.MapShape.{Circle, Rectangle}
import it.unibo.osmos.redux.mvc.model._
import it.unibo.osmos.redux.utils.Point

case class MovementSystem(levelInfo: Level) extends AbstractSystem[MovableProperty] {

  private val collisionRule = levelInfo.levelMap.collisionRule
  private val bounceRule = levelInfo.levelMap.mapShape match {
    case shape: Rectangle => RectangularBorder(Point(shape.center._1, shape.center._2), collisionRule, shape.base, shape.height)
    case shape: Circle => CircularBorder(Point(shape.center._1, shape.center._2), collisionRule, shape.radius)
    case _ => throw new IllegalArgumentException
  }

  override def getGroupProperty: Class[MovableProperty] = classOf[MovableProperty]

  override def update(): Unit = {
    entities foreach (entity => {
      updateEntitySpeed(entity)
      updateEntityPosition(entity)
      bounceRule.checkCollision(entity)
    })
  }

  private def updateEntitySpeed(entity: MovableProperty): Unit = {
    val accelerationComponent = entity.getAccelerationComponent
    val accelerationVector = accelerationComponent.vector
    val speedComponent = entity.getSpeedComponent
    val speedVector = speedComponent.vector
    speedComponent.vector_(speedVector.add(accelerationVector))
    accelerationComponent.reset()
  }

  private def updateEntityPosition(entity: MovableProperty): Unit = {
    val positionComponent = entity.getPositionComponent
    val position = positionComponent.point
    val speedVector = entity.getSpeedComponent.vector
    positionComponent.point_(position.add(speedVector))
  }
}
