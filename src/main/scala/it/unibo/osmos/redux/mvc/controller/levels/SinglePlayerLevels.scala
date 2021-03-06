package it.unibo.osmos.redux.mvc.controller.levels

import it.unibo.osmos.redux.mvc.controller.levels.structure.{CampaignLevel, CampaignLevelStat, LevelInfo}
import it.unibo.osmos.redux.mvc.controller.manager.files.UserProgressFileManager
import it.unibo.osmos.redux.mvc.view.events.{GameLost, GameStateEventWrapper, GameWon}
import it.unibo.osmos.redux.utils.Logger

/** Manages singlePlayer levels. */
object SinglePlayerLevels {
  implicit val who: String = "SinglePlayerLevels"
  private var levels: List[CampaignLevel] = List()

  /** Initialize singleton with single player levels.
    *
    * @param levelsInfo List of Option[LevelInfo]
    */
  def init(levelsInfo: List[Option[LevelInfo]]): Unit = {
    levels = levelsInfo.flatten.map(lvI => CampaignLevel(lvI.changeAvailability(), CampaignLevelStat()))
    levels.head.levelInfo.isAvailable = true
  }

  /** Return the last unlocked level.
    *
    * @return the last unlocked level.
    */
  def toDoLevel(campaignLevels: List[CampaignLevel] = levels): String = searchLastAvailableLevel() match {
    case Some(levelName) => levelName
    case _ => Logger.log("Error: campaign levels list is empty OR the last level had to be enabled")
      ""
  }

  /** Get the campaign levels info.
    *
    * @return List[LevelInfo]
    */
  def getLevelsInfo: List[LevelInfo] = levels.map(lv => lv.levelInfo)

  /** Get the campaign levels.
    *
    * @return List[CampaignLevel]
    */
  def getCampaignLevels: List[CampaignLevel] = levels

  /** Should be called when user win or lose a campaign level.
    *
    * @param endGame   end game result GameWon or GameLost.
    * @param levelName name of the played level.
    */
  def newEndGameEvent(endGame: GameStateEventWrapper, levelName: String): Unit = endGame match {
    case GameWon => increaseVictories(levelName)
      if (toDoLevel().equals(levelName)) unlockNextLevel()
    case GameLost => increaseDefeats(levelName)
    case _ => Logger.log("End game state not managed!")
  }

  /** Update campaign progress, should be called once when the game starts.
    *
    * @param campaignProgress campaign progress List[CampaignLevels]
    */
  def updateUserStat(campaignProgress: List[CampaignLevel]): Unit =
  /* if my values are less updated than the file ones */
    if (isMostRecent(toDoLevel(campaignProgress))) levels = campaignProgress

  /** Reset the user progress. */
  def reset(): Unit = {
    levels.head.levelStat = CampaignLevelStat()
    levels.filter(lv => !lv.levelInfo.name.equals(levels.head.levelInfo.name)).foreach(lv => {
      lv.levelInfo.isAvailable = false
      lv.levelStat = CampaignLevelStat()
    })
    UserProgressFileManager.saveUserProgress(levels)
    // Logger.log("reset done")
  }

  /** Unlock the next level. */
  private def unlockNextLevel(): Unit = levels.map(cLv => cLv.levelInfo).find(lv => !lv.isAvailable) match {
    case Some(nextLevel) => nextLevel.isAvailable = true
    case _ => // Logger.log("All levels are unlocked")
  }

  private def isMostRecent(loadedToDoLevelName: String) =
    getIndexOfLevelByName(toDoLevel()) <= getIndexOfLevelByName(loadedToDoLevelName)

  private def getIndexOfLevelByName(levelName: String) = levels.map(cLv => cLv.levelInfo.name).indexOf(levelName)

  private def searchLastAvailableLevel(cLevels: List[LevelInfo] = getLevelsInfo): Option[String] = cLevels match {
    case LevelInfo(lv, _, av) :: LevelInfo(_, _, av2) :: _ if av && !av2 => Some(lv)
    case List(LevelInfo(lv2, _, true)) => Some(lv2)
    case _ :: t => searchLastAvailableLevel(t)
    case _ => None
  }

  private def findLevelByName(levelName: String): Option[CampaignLevel] = levels.find(cLv => cLv.levelInfo.name.equals(levelName))

  private def increaseVictories(levelName: String): Unit = findLevelByName(levelName) match {
    case Some(cLevel: CampaignLevel) => cLevel.levelStat.victories += 1
    case None => Logger.log("Error: level " + levelName + " doesn't exists [IncreaseVictories]")
  }

  private def increaseDefeats(levelName: String): Unit = findLevelByName(levelName) match {
    case Some(cLevel: CampaignLevel) => cLevel.levelStat.defeats += 1
    case None => Logger.log("Error: level " + levelName + " doesn't exists [IncreaseDefeats]")
  }
}
