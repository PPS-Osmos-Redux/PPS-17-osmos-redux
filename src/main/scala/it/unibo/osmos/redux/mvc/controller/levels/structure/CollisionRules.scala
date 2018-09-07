package it.unibo.osmos.redux.mvc.controller.levels.structure

/**Map edges collision rules.*/
object CollisionRules extends Enumeration {
  val bouncing: CollisionRules.Value = Value("Bouncing")
  val instantDeath: CollisionRules.Value = Value("Instant_death")
}
