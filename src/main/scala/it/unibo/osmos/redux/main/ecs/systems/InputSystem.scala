package it.unibo.osmos.redux.main.ecs.systems

import it.unibo.osmos.redux.main.ecs.entities.{InputProperty, Property}
import it.unibo.osmos.redux.main.utils.InputEventStack

class InputSystem(priority: Int) extends System[InputProperty](priority) {

  /**
    * Acceleration coefficient to apply to each input movement
    */
  val accelCoefficient: Double = 0.01

  override def getGroupProperty(): Class[_ <: Property] = classOf[InputProperty]

  override def update(): Unit = {

    //retrieve all input events
    var inputEvents = InputEventStack.popAll()

    entities foreach (e => {
      val accel = e.getAccelerationComponent
      inputEvents foreach (_ => {

        //TODO: probably at some point there will be different events and it will be necessary to filter them before applying deceleration

        //apply negative mod to acceleration
        accel.accelerationX_(accel.accelerationX - accelCoefficient)
        accel.accelerationY_(accel.accelerationY - accelCoefficient)
      })
    })
  }
}
