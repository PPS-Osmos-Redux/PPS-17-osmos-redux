package it.unibo.osmos.redux.mvc.controller.levels.structure

/**
  * Level information
 *
  * @param name level name
  * @param isAvailable is the level is available
  * @param victoryRule level victory rule
  */
case class LevelInfo(var name: String, victoryRule: VictoryRules.Value,  var isAvailable: Boolean = true)
