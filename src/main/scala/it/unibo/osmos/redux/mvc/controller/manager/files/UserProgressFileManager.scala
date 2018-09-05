package it.unibo.osmos.redux.mvc.controller.manager.files

import java.io.File
import spray.json._
import spray.json.DefaultJsonProtocol._
import FileManager._
import it.unibo.osmos.redux.mvc.model.JsonProtocols._
import FileManager.{createDirectoriesTree, loadFile, saveToFile}
import UserHomePaths.userProgressDirectory
import it.unibo.osmos.redux.mvc.controller.{CampaignLevel, SinglePlayerLevels}

object UserProgressFileManager {
  /**
    * Saves user progress
    * @param userProgress current user progress
    * @return Option with file path of user progress file
    */
  def saveUserProgress(userProgress: List[CampaignLevel]): Boolean = {
    import UserHomePaths._
    val upFile = new File(defaultFS.getPath(userProgressDirectory + userProgressFileName + jsonExtension).toUri)
    createDirectoriesTree(upFile)
    saveToFile(upFile, userProgress.toJson.prettyPrint)
  }

  /**
    * Loads user progress from file
    * @return UserStat
    */
  def loadUserProgress(): List[CampaignLevel] =
    loadFile(userProgressDirectory + UserHomePaths.userProgressFileName + jsonExtension) match {
      case Some(text) => text.parseJson.convertTo[List[CampaignLevel]]
      case _ => saveUserProgress(SinglePlayerLevels.getCampaignLevels)
        loadUserProgress()
    }
}
