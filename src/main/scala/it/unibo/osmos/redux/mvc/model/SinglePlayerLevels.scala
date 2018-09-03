package it.unibo.osmos.redux.mvc.model

import it.unibo.osmos.redux.mvc.controller.LevelInfo
import it.unibo.osmos.redux.mvc.view.events.{GameLost, GameStateEventWrapper, GameWon}
import it.unibo.osmos.redux.utils.Logger

import scala.collection.mutable

object SinglePlayerLevels {
  implicit val who:String = "SinglePlayerLevels"
  private val levels:mutable.ArraySeq[CampaignLevel] = mutable.ArraySeq(
    CampaignLevel(LevelInfo("1", VictoryRules.becomeTheBiggest), LevelStat(0,0)),
    CampaignLevel(LevelInfo("2", VictoryRules.becomeTheBiggest, isAvailable = false), LevelStat(0,0)),
    CampaignLevel(LevelInfo("3", VictoryRules.becomeTheBiggest, isAvailable = false), LevelStat(0,0)),
    CampaignLevel(LevelInfo("4", VictoryRules.becomeTheBiggest, isAvailable = false), LevelStat(0,0)),
    CampaignLevel(LevelInfo("5", VictoryRules.becomeTheBiggest, isAvailable = false), LevelStat(0,0)))

  /**
    * get the campaign levels
    * @return campaign levels
    */
  def getLevelsInfo:List[LevelInfo] = levels.map(lv => lv.levelInfo).toList

  def getCampaignLevels:List[CampaignLevel] = levels.toList

  /**
    * reset the user progress
    */
  def reset():Unit ={
    levels.head.levelStat = LevelStat(0,0)
    //TODO: delete user progress file
    levels.filter(lv => !lv.levelInfo.name.equals(levels.head.levelInfo.name)).foreach(lv => {
      lv.levelInfo.isAvailable = false
      lv.levelStat = LevelStat(0,0)
    })
  }


  /**
    * Return the last unlocked level.
    * @return the last unlocked level
    */
  def toDoLevel(campaignLevels:List[CampaignLevel] = levels.toList):String = {
    def searchLastAvailableLevel(cLevels:List[LevelInfo]):Option[String] = cLevels match {
      case LevelInfo(lv:String, _, av:Boolean)+:LevelInfo(_:String, _, av2:Boolean)+:_ if av && !av2 =>
        Some(lv)
      case LevelInfo(lv:String, _, av:Boolean)+:List(LevelInfo(lv2:String, _, av2:Boolean)) =>
        if(av && !av2) Some(lv) else Some (lv2)
      case _+:t => searchLastAvailableLevel(t)
      case _ => Logger.log("Error: single player levels list is empty")
                None
    }
    searchLastAvailableLevel(campaignLevels.map(cLv => cLv.levelInfo)).get
  }

  /**
    * Unlock the next level
    */
  private def unlockNextLevel():Unit = levels.map(cLv => cLv.levelInfo)
                                             .filter(lv => !lv.isAvailable)
                                             .foreach(level => {
                                               level.isAvailable = true
                                               return
                                             })

  def newEndGameEvent(endGame:GameStateEventWrapper, levelName:String): Unit = endGame match {
    case GameWon => levels.find(cLv => cLv.levelInfo.name.equals(levelName)).get.levelStat.victories+=1
                    unlockNextLevel()
    case GameLost => levels.find(cLv => cLv.levelInfo.name.equals(levelName)).get.levelStat.defeats+=1
    case _ =>
  }

  private def updateLevels(toDoLevel:String):Unit = levels.map(cLv => cLv.levelInfo)
                                                          .foreach(level => {
                                                            if(level.name.equals(toDoLevel)) return
                                                              unlockNextLevel()
                                                           })
  /**
    * Method for update application user progression.
    * Used for update user stat
    * @param loadedUserStat user stat
    */
  def updateUserStat(campaignProgress: List[CampaignLevel]): Unit ={
    /* if my values are less updated than the file ones */
    if(levels.map(l => l.levelInfo.name).indexOf(toDoLevel(campaignProgress)) < levels.map(l => l.levelInfo.name).indexOf(toDoLevel(campaignProgress))) {
      updateLevels(toDoLevel(campaignProgress))
    }
  }
}

case class CampaignLevel(levelInfo: LevelInfo, var levelStat: LevelStat)
