package it.unibo.osmos.redux.main.ecs.systems

import it.unibo.osmos.redux.main.ecs.entities.{MovableProperty, Property}
import it.unibo.osmos.redux.main.utils.Point

case class MovementSystem(override val priority: Int) extends System[MovableProperty](priority) {

  override def getGroupProperty: Class[_ <: Property] = classOf[MovableProperty]

  override def update(): Unit = {
    entities.foreach(entity => {
      updateEntitySpeed(entity)
      updateEntityPosition(entity)
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
