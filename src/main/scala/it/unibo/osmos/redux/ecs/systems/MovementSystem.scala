package it.unibo.osmos.redux.ecs.systems

import it.unibo.osmos.redux.ecs.entities.properties.composed.MovableProperty
import it.unibo.osmos.redux.utils.Constants

/** System manging cell movement based on their acceleration and speed */
case class MovementSystem() extends AbstractSystem[MovableProperty] {

  override def update(): Unit = {
    entities foreach (entity => {
      updateEntitySpeed(entity)
      updateEntityPosition(entity)
    })
  }

  private def updateEntitySpeed(entity: MovableProperty): Unit = {
    val accelerationComponent = entity.getAccelerationComponent
    val accelerationVector = accelerationComponent.vector
    val speedComponent = entity.getSpeedComponent
    val speedVector = speedComponent.vector
    speedComponent.vector_(speedVector.add(accelerationVector).limit(Constants.General.CellMaxSpeed))
    accelerationComponent.reset()
  }

  private def updateEntityPosition(entity: MovableProperty): Unit = {
    val positionComponent = entity.getPositionComponent
    val position = positionComponent.point
    val speedVector = entity.getSpeedComponent.vector
    positionComponent.point_(position.add(speedVector))
  }
}
