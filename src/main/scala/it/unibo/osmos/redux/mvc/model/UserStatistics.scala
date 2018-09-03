package it.unibo.osmos.redux.mvc.model

/**
  * Statistics of a level
  * @param levelName level name
  * @param defeats number of defeats
  * @param victories number of victories
  */

case class LevelStat(/*levelName:String, */var defeats:Int, var victories: Int)

/**
  * Modelling user progression and stat
  * @param toDoLevel last unlocked level in campaign mode
  * @param campaignStats list of levels statistics in campaign mode
  */
//case class UserStat(toDoLevel:String = "1", campaignStats:List[LevelStat] = SinglePlayerLevels.getLevels.map(lv => LevelStat(lv.name, 0, 0)))
