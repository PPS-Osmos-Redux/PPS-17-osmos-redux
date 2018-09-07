package it.unibo.osmos.redux.mvc.controller.levels

import it.unibo.osmos.redux.mvc.controller.levels.structure.{LevelInfo, VictoryRules}

/**Manages multiPlayer levels.*/
object MultiPlayerLevels {
  private var levels:List[LevelInfo] = List()
  def getLevels:List[LevelInfo] = levels

  /** Initialize singleton with multiPlayer levels.
    *
    * @param levelsInfo List of Option[LevelInfo]
    */
  def init(levelsInfo:List[Option[LevelInfo]]): Unit =levels = levelsInfo.flatten
}
