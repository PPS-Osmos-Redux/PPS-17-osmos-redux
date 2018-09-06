package it.unibo.osmos.redux.mvc.controller.levels

import it.unibo.osmos.redux.mvc.controller.levels.structure.LevelInfo

object MultiPlayerLevels {
  private var levels:List[LevelInfo] = List()
  def getLevels:List[LevelInfo] = levels

  /**
    * Initialize levels
    * @param levelsInfo Level[Info]
    */
  def init(levelsInfo:List[Option[LevelInfo]]): Unit =
    levelsInfo.filter(lv => lv isDefined).foreach(lvInfo => levels = lvInfo.get::levels)
}