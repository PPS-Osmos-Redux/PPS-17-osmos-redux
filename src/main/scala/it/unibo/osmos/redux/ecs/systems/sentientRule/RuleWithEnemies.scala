package it.unibo.osmos.redux.ecs.systems.sentientRule

import it.unibo.osmos.redux.ecs.entities.SentientEnemyProperty

import scala.collection.mutable.ListBuffer

abstract class RuleWithEnemies(enemies: ListBuffer[SentientEnemyProperty]) extends SentientRule {

}
