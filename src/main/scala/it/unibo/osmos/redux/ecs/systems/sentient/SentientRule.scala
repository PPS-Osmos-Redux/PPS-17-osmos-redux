package it.unibo.osmos.redux.ecs.systems.sentient

import it.unibo.osmos.redux.ecs.entities.SentientProperty
import it.unibo.osmos.redux.utils.Vector


trait SentientRule {

  def computeRule(sentient: SentientProperty, previousAcceleration: Vector): Vector
}
