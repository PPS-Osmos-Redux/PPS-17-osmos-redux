package it.unibo.osmos.redux.ecs.components

import it.unibo.osmos.redux.utils

/**
  * Component of the acceleration vector
  */
trait AccelerationComponent extends VectorComponent {

  /**
    * Getter. Return the acceleration of the x coordinate
    *
    * @return the acceleration
    */
  //def x: Double

  /**
    * Getter. Return the acceleration of the y coordinate
    *
    * @return the acceleration
    */
  //def y: Double

  /**
    * Setter. Set the new acceleration of the x coordinate
    *
    * @param acceleration the new acceleration
    */
  //def x_(acceleration: Double): Unit

  /**
    * Setter. Set the new acceleration of the y coordinate
    *
    * @param acceleration the new acceleration
    */
  //def y_(acceleration: Double): Unit
}

object AccelerationComponent {
  def apply(accelerationX: Double, accelerationY: Double): AccelerationComponent = AccelerationComponentImpl(utils.Vector(accelerationX, accelerationY))

  private case class AccelerationComponentImpl(var _speedVector: utils.Vector) extends AccelerationComponent {
    /*override def x: Double = _accelerationX

    override def y: Double = _accelerationY

    override def x_(acceleration: Double): Unit = _accelerationX = acceleration

    override def y_(acceleration: Double): Unit = _accelerationY = acceleration*/

    override def vector: utils.Vector = _speedVector

    override def vector_(vector: utils.Vector): Unit = _speedVector = vector
  }

}
