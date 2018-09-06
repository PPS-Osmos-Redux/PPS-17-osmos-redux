package it.unibo.osmos.redux.mvc.controller.levels.manager

import it.unibo.osmos.redux.mvc.controller.levels.structure.{CampaignLevel, CampaignLevelStat, LevelInfo, VictoryRules}
import it.unibo.osmos.redux.mvc.controller.manager.files.UserProgressFileManager
import it.unibo.osmos.redux.mvc.view.events.{GameLost, GameStateEventWrapper, GameWon}
import it.unibo.osmos.redux.utils.Logger

object SinglePlayerLevels {
  implicit val who:String = "SinglePlayerLevels"
  private var levels:List[CampaignLevel] = List(
    CampaignLevel(LevelInfo("1", VictoryRules.becomeTheBiggest), CampaignLevelStat()),
    CampaignLevel(LevelInfo("2", VictoryRules.becomeTheBiggest, isAvailable = false), CampaignLevelStat()),
    CampaignLevel(LevelInfo("3", VictoryRules.becomeTheBiggest, isAvailable = false), CampaignLevelStat()),
    CampaignLevel(LevelInfo("4", VictoryRules.becomeTheBiggest, isAvailable = false), CampaignLevelStat()),
    CampaignLevel(LevelInfo("5", VictoryRules.becomeTheBiggest, isAvailable = false), CampaignLevelStat()))

  private var spLevels:List[CampaignLevel] = List()

  /**
    * Load campaign levels, this method use default values for stats
    * @param campaignLevels loaded from resources campaign levels info
    */
  def loadCampaignLevels(campaignLevels:List[LevelInfo]): Unit =
    campaignLevels.foreach(lvInfo => spLevels = CampaignLevel(lvInfo)::spLevels)

  /**
    * Return the last unlocked level.
    * @return the last unlocked level
    */
  def toDoLevel(campaignLevels:List[CampaignLevel] = levels):String = searchLastAvailableLevel() match {
    case Some(levelName) => levelName
    case _ => Logger.log("Error: campaign levels list is empty OR the last level had to be enabled")
              ""
  }

  /**
    * Unlock the next level
    */
  private def unlockNextLevel():Unit =  levels.map(cLv => cLv.levelInfo).find(lv => !lv.isAvailable) match {
    case Some(nextLevel) => nextLevel.isAvailable = true
    case _ => Logger.log("All levels are unlocked")
  }

  /**
    * get the campaign levels info
    * @return List[LevelInfo]
    */
  def getLevelsInfo:List[LevelInfo] = levels.map(lv => lv.levelInfo)

  /**
    * get the campaign levels
    * @return List[CampaignLevel]
    */
  def getCampaignLevels:List[CampaignLevel] = levels

  /**
    * Should be called when user win or lose a campaign level
    * @param endGame end game result GameWon or GameLost
    * @param levelName name of the played lavel
    */
  def newEndGameEvent(endGame:GameStateEventWrapper, levelName:String): Unit = endGame match {
    case GameWon => increaseVictories(levelName)
                    if(toDoLevel().equals(levelName)) unlockNextLevel()
    case GameLost => increaseDefeats(levelName)
    case _ =>
  }

  /**
    * Method for update campaign progress, should be  called once when the game starts
    * @param campaignProgress campaign progress List[CampaignLevels]
    */
  def updateUserStat(campaignProgress: List[CampaignLevel]): Unit =
  /* if my values are less updated than the file ones */
    if(isMostRecent(toDoLevel(campaignProgress))) levels = campaignProgress

  /**
    * reset the user progress
    */
  def reset():Unit ={
    levels.head.levelStat = CampaignLevelStat()
    levels.filter(lv => !lv.levelInfo.name.equals(levels.head.levelInfo.name)).foreach(lv => {
      lv.levelInfo.isAvailable = false
      lv.levelStat = CampaignLevelStat()
    })
    UserProgressFileManager.saveUserProgress(levels)
  }

  private def isMostRecent(loadedToDoLevelName:String) =
    getIndexOfLevelByName(toDoLevel()) <= getIndexOfLevelByName(loadedToDoLevelName)

  private def getIndexOfLevelByName(levelName:String) = levels.map(cLv => cLv.levelInfo.name).indexOf(levelName)

  private def searchLastAvailableLevel(cLevels:List[LevelInfo] = getLevelsInfo):Option[String] = cLevels match {
    case LevelInfo(lv, _, av)::LevelInfo(_, _, av2)::_ if av && !av2 => Some(lv)
    case List(LevelInfo(lv2, _, true)) => Some (lv2)
    case _::t => searchLastAvailableLevel(t)
    case _ => None
  }

  private def findLevelByName(levelName:String):Option[CampaignLevel] = levels.find(cLv => cLv.levelInfo.name.equals(levelName))

  private def increaseVictories(levelName:String): Unit = findLevelByName(levelName) match {
    case Some(cLevel:CampaignLevel) => cLevel.levelStat.victories+=1
    case None => Logger.log("Error: level " + levelName + " doesn't exists [IncreaseVictories]")
  }

  private def increaseDefeats(levelName:String): Unit = findLevelByName(levelName) match {
    case Some(cLevel:CampaignLevel) => cLevel.levelStat.defeats+=1
    case None => Logger.log("Error: level " + levelName + " doesn't exists [IncreaseDefeats]")
  }
}
