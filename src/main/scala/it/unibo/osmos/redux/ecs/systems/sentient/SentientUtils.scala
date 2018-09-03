package it.unibo.osmos.redux.ecs.systems.sentient

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
}
