package it.unibo.osmos.redux.mvc.model

import it.unibo.osmos.redux.mvc.controller.LevelInfo.LevelInfo
import it.unibo.osmos.redux.mvc.view.events.{GameLost, GameStateEventWrapper, GameWon}
import it.unibo.osmos.redux.utils.Logger

import scala.collection.mutable

object SinglePlayerLevels {
  implicit val who:String = "SinglePlayerLevels"
  private var userStat:UserStat = UserStat()
  private val levels:mutable.ArraySeq[LevelInfo] = mutable.ArraySeq(
    LevelInfo("1", VictoryRules.becomeTheBiggest),
    LevelInfo("2", VictoryRules.becomeTheBiggest, isAvailable = false),
    LevelInfo("3", VictoryRules.becomeTheBiggest, isAvailable = false),
    LevelInfo("4", VictoryRules.becomeTheBiggest, isAvailable = false),
    LevelInfo("5", VictoryRules.becomeTheBiggest, isAvailable = false))

  /**
    * get the campaign levels
    * @return campaign levels
    */
  def getLevels:List[LevelInfo] = levels.toList

  /**
    * reset the user progress
    */
  def reset():Unit = {
    levels.filter(lv => !lv.name.equals(levels.head.name)).foreach(lv => lv.isAvailable = false)
    userStat = UserStat()
  }

  /**
    * Return the last unlocked level.
    * @return the last unlocked level
    */
  def toDoLevel:String = {
    def searchLastAvailableLevel(levelsList:mutable.ArraySeq[LevelInfo]):Option[String] = levelsList match {
      case LevelInfo(lv:String, _, av:Boolean)+:LevelInfo(_:String, _, av2:Boolean)+:_ if av && !av2 =>
        Some(lv)
      case LevelInfo(lv:String, _, av:Boolean)+:mutable.ArraySeq(LevelInfo(lv2:String, _, av2:Boolean)) =>
        if(av && !av2) Some(lv) else Some (lv2)
      case _+:t => searchLastAvailableLevel(t)
      case _ => Logger.log("Error: single player levels list is empty")
                None
    }
    searchLastAvailableLevel(levels).get
  }

  /**
    * Unlock the next level
    */
  private def unlockNextLevel():Unit = levels.filter(lv => !lv.isAvailable)
                                     .foreach(level => {
                                       levels.update(levels.indexOf(level),
                                         LevelInfo(level.name, level.victoryRule))
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

  def userStatistics:UserStat = userStat
}
