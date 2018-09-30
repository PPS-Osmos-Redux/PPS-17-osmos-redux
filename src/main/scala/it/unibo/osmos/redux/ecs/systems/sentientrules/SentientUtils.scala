package it.unibo.osmos.redux.ecs.systems.sentientrules

import it.unibo.osmos.redux.ecs.entities.properties.composed.SentientProperty
import it.unibo.osmos.redux.utils.Constants.Sentient._
import it.unibo.osmos.redux.utils.Vector

/** Utils for sentient system and rules */
object SentientUtils {

  /** compute the acceleration to apply to the actual velocity and obtain the desired velocity,
    * then limit it
    *
    * @param actualVelocity  actual velocity
    * @param desiredVelocity desired velocity
    * @return the limited acceleration
    */
  def computeSteer(actualVelocity: Vector, desiredVelocity: Vector): Vector =
    computeUnlimitedSteer(actualVelocity, desiredVelocity) limit MaxAcceleration

  /** compute the acceleration to apply to the actual velocity and obtain the desired velocity
    *
    * @param actualVelocity  actual velocity
    * @param desiredVelocity desired velocity
    * @return the acceleration
    */
  def computeUnlimitedSteer(actualVelocity: Vector, desiredVelocity: Vector): Vector =
    desiredVelocity multiply MaxSpeed subtract actualVelocity

  /** Return the desired acceleration from a sentient to other enemies
    *
    * @param actualVelocity sentient's velocity
    * @return desired separation
    */
  def getDesiredSeparation(actualVelocity: Vector): Double =
    CoefficientDesiredSeparation + actualVelocity.getMagnitude / MaxAcceleration

  /** check which behaviour have the sentient
    *
    * @param sentient sentient to check
    * @return true if the behaviour is lost radius, false otherwise
    */
  def hasLostRadiusBehaviour(sentient: SentientProperty): Boolean =
    sentient.getDimensionComponent.radius >= MinRadiusForLostRadiusBehaviour

  implicit class PimpedList[A](list: List[(A, Double)]) {

    /** Find the smallest distance(second parameter of tuple value) inside the list,
      * if it is less or equal than threshold, then
      * shift all distance of minus smallest distance plus threshold, so that
      * the smallest distance is equal to threshold
      *
      * @param threshold the threshold
      * @return shifted list
      */
    def shiftDistance(threshold: Double): List[(A, Double)] = list match {
      case Nil => Nil
      case _ => list.min(Ordering.by((d: (A, Double)) => d._2)) match {
        case min if min._2 <= threshold => list.map(e => (e._1, e._2 - min._2 + threshold))
        case _ => list
      }
    }
  }

}
