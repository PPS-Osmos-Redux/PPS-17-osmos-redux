package it.unibo.osmos.redux.mvc.model

/**
  * Statistics of a level
  * @param levelName level name
  * @param defeats number of defeats
  * @param victories number of victories
  */

case class LevelStat(levelName:String, var defeats:Int, var victories: Int)

/**
  * Modelling user progression and stat
  * @param toDoLevel last unlocked level
  * @param stats list of levels statistics
  */
case class UserStat(toDoLevel:String = "1", stats:List[LevelStat] = List(LevelStat("1", 0, 0),
  LevelStat("2", 0, 0),
  LevelStat("3", 0, 0),
  LevelStat("4", 0, 0),
  LevelStat("5", 0, 0)))
