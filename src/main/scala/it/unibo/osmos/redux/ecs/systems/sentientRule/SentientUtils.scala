package it.unibo.osmos.redux.ecs.systems.sentientRule

import it.unibo.osmos.redux.ecs.entities.SentientProperty
import it.unibo.osmos.redux.utils.Vector
import it.unibo.osmos.redux.utils.Constants.Sentient._

object SentientUtils {

  def computeSteer(actualVelocity: Vector, desiredVelocity: Vector): Vector =
    computeUnlimitedSteer(actualVelocity, desiredVelocity) limit MAX_ACCELERATION

  def computeUnlimitedSteer(actualVelocity: Vector, desiredVelocity: Vector): Vector =
    desiredVelocity multiply MAX_SPEED subtract actualVelocity

  def getDesiredSeparation(actualVelocity: Vector) : Double =
    COEFFICIENT_DESIRED_SEPARATION + actualVelocity.getMagnitude / MAX_ACCELERATION

  def hasLostRadiusBehaviour(sentient: SentientProperty): Boolean =
    sentient.getDimensionComponent.radius >= MIN_RADIUS_FOR_LOST_RADIUS_BEHAVIOUR

  implicit class PimpedList[A](list: List[(A, Double)]) {

    /**
      * Find the smallest distance(second parameter of tuple value) inside the list,
      * if it is less or equal than threshold, then
      * shift all distance of minus smallest distance plus threshold, so that
      * the smallest distance is equal to threshold
      * @param threshold the threshold
      * @return shifted list
      */
    def shiftDistance(threshold: Double): List[(A, Double)] = list match {
      case Nil => Nil
      case _ => list.min(Ordering.by((d:(A, Double)) => d._2)) match {
        case min if min._2 <= threshold => list.map(e => (e._1, e._2 - min._2 + threshold))
        case _ => list
      }
    }
  }
}
