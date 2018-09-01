package it.unibo.osmos.redux.mvc.model

import it.unibo.osmos.redux.mvc.view.events.{GameLost, GameStateEventWrapper, GameWon}

import scala.collection.mutable

object SinglePlayerLevels {
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
    * Throws exception if the levels list is empty
    * @return the last unlocked level
    */
  @throws(classOf[MatchError])
  def toDoLevel:String = {
    @throws(classOf[MatchError])
    def searchLastAvailableLevel(levelsList:mutable.ArraySeq[LevelInfo]):Option[String] = levelsList match {
      case LevelInfo(lv:String, _, av:Boolean)+:LevelInfo(_:String, _, av2:Boolean)+:_ if av && !av2 =>
        Some(lv)
      case LevelInfo(lv:String, _, av:Boolean)+:mutable.ArraySeq(LevelInfo(lv2:String, _, av2:Boolean)) =>
        if(av && !av2) Some(lv) else Some (lv2)
      case _+:t => searchLastAvailableLevel(t)
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
  case class LevelInfo(name:String, victoryRule:VictoryRules.Value,  var isAvailable:Boolean = true)

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
