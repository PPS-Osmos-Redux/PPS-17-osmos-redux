package it.unibo.osmos.redux.mvc.model

import it.unibo.osmos.redux.mvc.controller.FileManager
import it.unibo.osmos.redux.mvc.view.events.{GameLost, GameStateEventWrapper, GameWon}

import scala.collection.mutable

object SinglePlayerLevels {
  private var userStat:UserStat = UserStat()
  private val levels:mutable.ArraySeq[LevelInfo] = mutable.ArraySeq(
    LevelInfo("1", isAvailable = true, VictoryRules.becomeTheBiggest),
    LevelInfo("2", isAvailable = false, VictoryRules.becomeTheBiggest),
    LevelInfo("3", isAvailable = false, VictoryRules.becomeTheBiggest),
    LevelInfo("4", isAvailable = false, VictoryRules.becomeTheBiggest),
    LevelInfo("5", isAvailable = false, VictoryRules.becomeTheBiggest))

  def getLevels:List[LevelInfo] = levels.toList

  /**
    * Return the last unlocked level.
    * @return the last unlocked level
    */
  def toDoLevel:LevelInfo = levels.find(level => !level.isAvailable).getOrElse(levels(levels.size-1))

  /**
    * Unlock the next level
    */
  def unlockNextLevel():Unit = levels.filter(lv => !lv.isAvailable)
                                     .foreach(level => {
                                       levels.update(levels.indexOf(level),
                                         LevelInfo(level.name, isAvailable = true, level.victoryRule))
                                       return
                                     })

  def newEndGameEvent(endGame:GameStateEventWrapper, levelName:String): Unit = endGame match {
    case GameWon => userStat.stats.find(lvl => lvl.levelName.equals(levelName)).get.victories+=1
                    unlockNextLevel()
    case GameLost => userStat.stats.find(lvl => lvl.levelName.equals(levelName)).get.defeats+=1
    case _ =>
  }

  private def updateLevels(toDoLevel:String):Unit = levels.foreach(level => {
                                                             if(level.name.equals(toDoLevel)) return
                                                               unlockNextLevel()
                                                             })
  /**
    * Method for update application user progression.
    * Used for update user stat
    * @param loadedUserStat user stat
    */
  def updateUserStat(loadedUserStat: UserStat): Unit ={
    /* if my values are less updated than the file ones */
    if(levels.map(l => l.name).indexOf(userStat.toDoLevel) < levels.map(l => l.name).indexOf(loadedUserStat.toDoLevel)) {
      updateLevels(loadedUserStat.toDoLevel)
      userStat = loadedUserStat
    }
  }

  /**
    * Return current user stat
    * @return UserStat
    */
  def userStatistics():UserStat = UserStat(userStat.toDoLevel, userStat.stats)

  /**
    * Level information
    * @param name level name
    * @param isAvailable is the level is available
    * @param victoryRule level victory rule
    */
  case class LevelInfo(name:String, isAvailable:Boolean = false, victoryRule:VictoryRules.Value)

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
}
