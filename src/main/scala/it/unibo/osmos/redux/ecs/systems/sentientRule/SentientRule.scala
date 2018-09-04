package it.unibo.osmos.redux.ecs.systems.sentientRule

import it.unibo.osmos.redux.ecs.entities.properties.composed.SentientProperty
import it.unibo.osmos.redux.utils.Vector


trait SentientRule {

  def computeRule(sentient: SentientProperty, previousAcceleration: Vector): Vector
}
