package it.unibo.osmos.redux.mvc.model

import it.unibo.osmos.redux.mvc.controller.LevelInfo.LevelInfo

import scala.collection.mutable

object MultiPlayerLevels {
  private val levels:mutable.ArraySeq[LevelInfo] = mutable.ArraySeq(
    LevelInfo("1", VictoryRules.absorbAllOtherPlayers, isAvailable = false),
    LevelInfo("2", VictoryRules.absorbAllOtherPlayers, isAvailable = false),
    LevelInfo("3", VictoryRules.absorbAllOtherPlayers, isAvailable = false),
    LevelInfo("4", VictoryRules.absorbAllOtherPlayers, isAvailable = false),
    LevelInfo("5", VictoryRules.absorbAllOtherPlayers, isAvailable = false))
  def getLevels:List[LevelInfo] = levels.toList
}
