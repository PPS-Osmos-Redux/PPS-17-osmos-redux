package it.unibo.osmos.redux.mvc.model

import scala.collection.mutable

object SinglePlayerLevels {
  //private val levels:mutable.ArraySeq[(String,Boolean)] = mutable.ArraySeq("1" -> true, "2" -> false, "3" -> false, "4" -> false, "5" -> false)

  private val levels:mutable.ArraySeq[LevelInfo] = mutable.ArraySeq(
    LevelInfo("1", isAvailable = true, VictoryRules.becomeTheBiggest),
    LevelInfo("2", isAvailable = false, VictoryRules.becomeTheBiggest),
    LevelInfo("3", isAvailable = false, VictoryRules.becomeTheBiggest),
    LevelInfo("4", isAvailable = false, VictoryRules.becomeTheBiggest),
    LevelInfo("5", isAvailable = false, VictoryRules.becomeTheBiggest))

  def getLevels:List[LevelInfo] = levels.toList

  /**
    * Return the last unlocked level.
    * Throws exception if the levels list is empty
    * @return the last unlocked level
    */
  @throws(classOf[MatchError])
  def toDoLevel:String = {
    @throws(classOf[MatchError])
    def searchLastAvailableLevel(levelsList:mutable.ArraySeq[LevelInfo]):Option[String] = levelsList match {
      case LevelInfo(lv:String,av:Boolean, _)+:LevelInfo(_:String,av2:Boolean, _)+:_ if av && !av2 =>
        Some(lv)
      case LevelInfo(lv:String,av:Boolean,_)+:mutable.ArraySeq(LevelInfo(lv2:String,av2:Boolean, _)) =>
        if(av && !av2) Some(lv) else Some (lv2)
      case _+:t => searchLastAvailableLevel(t)
    }
    searchLastAvailableLevel(levels).get
  }

  /**
    * Unlock the next level
    */
  def unlockNextLevel():Unit = levels.filter(lv => !lv.isAvailable)
                                     .foreach(level => {
                                       levels.update(levels.indexOf(level),
                                         LevelInfo(level.name, isAvailable = true, level.victoryRule))
                                       return
                                     })

  /**
    * Method for update application user progression.
    * Used for synchronize application and file user progression
    * @param userStat user stat
    */
  def syncWithFile(userStat: UserStat): Unit ={
    levels.foreach(level => {
      if(level.name.equals(userStat.toDoLevel)) return
      unlockNextLevel()
    })
  }

  /**
    * Method for convert current user progression into an object
    * @return UserStat
    */
  def toUserProgression:UserStat = UserStat(toDoLevel)

  /**
    * Modelling user progression and stat
    * @param toDoLevel last unlocked level
    */
  case class UserStat(toDoLevel:String = "1")
  case class LevelInfo(name:String, isAvailable:Boolean = false, victoryRule:VictoryRules.Value)
}
