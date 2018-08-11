package it.unibo.osmos.redux.main.ecs.systems

import it.unibo.osmos.redux.main.ecs.entities.{InputProperty, Property}
import it.unibo.osmos.redux.main.utils.{InputEventStack, MathUtils, Point}

case class InputSystem(override val priority: Int) extends System[InputProperty](priority) {

  /**
    * Acceleration coefficient to apply to each input movement
    */
  val accelCoefficient: Double = 0.1

  override def getGroupProperty(): Class[_ <: Property] = classOf[InputProperty]

  override def update(): Unit = {

    //retrieve all input events
    var inputEvents = InputEventStack.popAll()

    entities foreach (e => {
      val accel = e.getAccelerationComponent
      val pos = e.getPositionComponent

      inputEvents foreach (ev => {

        //TODO: probably at some point there will be different events and it will be necessary to filter them before applying deceleration

        val newPoint = MathUtils.normalizePoint(Point(pos.point.x - ev.point.x, pos.point.y - ev.point.y))

        //apply negative mod to acceleration
        accel.accelerationX_(accel.accelerationX + newPoint.x * accelCoefficient)
        accel.accelerationY_(accel.accelerationY + newPoint.y * accelCoefficient)
      })
    })
  }
}
