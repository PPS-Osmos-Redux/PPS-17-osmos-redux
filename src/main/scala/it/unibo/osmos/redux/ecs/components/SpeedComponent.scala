package it.unibo.osmos.redux.ecs.components

import it.unibo.osmos.redux.utils

/**
  * Component of the speed vector
  */
trait SpeedComponent extends VectorComponent {

  /**
    * Getter. Return the speed of the _speedX coordinate
    *
    * @return the speed
    */
  // def x: Double

  /**
    * Getter. Return the speed of the _speedY coordinate
    *
    * @return the speed
    */
  // def y: Double

  /**
    * Setter. Set the new speed of the _speedX coordinate
    *
    * @param speed the new speed
    */
  // def x_(speed: Double): Unit

  /**
    * Setter. Set the new speed of the _speedY coordinate
    *
    * @param speed the new speed
    */
  // def y_(speed: Double): Unit
}

object SpeedComponent {
  def apply(speedX: Double, speedY: Double): SpeedComponent = SpeedComponentImpl(utils.Vector(speedX, speedY))

  private case class SpeedComponentImpl(var _speedVector: utils.Vector) extends SpeedComponent {
    override def vector: utils.Vector = _speedVector

    override def vector_(vector: utils.Vector): Unit = _speedVector = vector

    /*override def x: Double = _speedX

    override def y: Double = _speedY

    override def x_(speed: Double): Unit = _speedX = speed

    override def y_(speed: Double): Unit = _speedY = speed*/

  }

}
