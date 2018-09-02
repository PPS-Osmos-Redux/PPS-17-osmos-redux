package it.unibo.osmos.redux.mvc.controller

import it.unibo.osmos.redux.mvc.model.{Level, VictoryRules}

object LevelInfo {
  def apply(level: Level): LevelInfo = LevelInfo(level.levelId, level.victoryRule)

  /**
    * Level information
    * @param name level name
    * @param isAvailable is the level is available
    * @param victoryRule level victory rule
    */
  case class LevelInfo(name:String, victoryRule:VictoryRules.Value,  var isAvailable:Boolean = true)
}
