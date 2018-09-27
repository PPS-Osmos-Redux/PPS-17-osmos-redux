package it.unibo.osmos.redux.ecs.systems.sentientrules

import it.unibo.osmos.redux.ecs.entities.properties.composed.SentientProperty
import it.unibo.osmos.redux.utils.Vector

/** Base rule for sentient entity */
trait SentientRule {

  /** compute the acceleration to apply for this rule to the specified entity with the previous compute acceleration
    *
    * @param sentient             entity to apply the rule
    * @param previousAcceleration previous acceleration compute
    * @return acceleration to apply this rule considered the previous acceleration
    */
  def computeRule(sentient: SentientProperty, previousAcceleration: Vector): Vector
}
