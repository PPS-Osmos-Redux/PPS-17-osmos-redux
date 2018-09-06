package it.unibo.osmos.redux.mvc.controller.levels.manager

import it.unibo.osmos.redux.mvc.controller.levels.structure.{LevelInfo, VictoryRules}

import scala.collection.mutable

object MultiPlayerLevels {
  private val levels:mutable.ArraySeq[LevelInfo] = mutable.ArraySeq(
    LevelInfo("1", VictoryRules.absorbAllOtherPlayers),
    LevelInfo("2", VictoryRules.absorbAllOtherPlayers),
    LevelInfo("3", VictoryRules.absorbAllOtherPlayers),
    LevelInfo("4", VictoryRules.absorbAllOtherPlayers),
    LevelInfo("5", VictoryRules.absorbAllOtherPlayers))
  def getLevels:List[LevelInfo] = levels.toList
}
