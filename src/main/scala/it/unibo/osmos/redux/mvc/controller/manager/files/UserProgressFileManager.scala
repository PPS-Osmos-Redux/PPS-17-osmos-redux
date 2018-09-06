package it.unibo.osmos.redux.mvc.controller.manager.files

import java.io.File
import java.nio.file.Paths

import it.unibo.osmos.redux.mvc.controller.levels.SinglePlayerLevels
import it.unibo.osmos.redux.mvc.controller.levels.structure.CampaignLevel
import it.unibo.osmos.redux.mvc.model.JsonProtocols._
import spray.json.DefaultJsonProtocol._
import spray.json._
import it.unibo.osmos.redux.utils.Constants.UserHomePaths._
import it.unibo.osmos.redux.utils.Logger

import scala.util.{Failure, Success, Try}

object UserProgressFileManager extends FileManager {
  override implicit val who: String = "UserProgressFileManager"

  /**
    * Saves user progress
    * @param userProgress current user progress
    * @return Option with file path of user progress file
    */
  def saveUserProgress(userProgress: List[CampaignLevel]): Boolean = {
    val upFile = new File(defaultFS.getPath(userProgressFileName + jsonExtension).toUri)
    createDirectoriesTree(upFile)
    saveToFile(upFile, userProgress.toJson.prettyPrint)
  }

  /**
    * Loads user progress from file
    * @return UserStat
    */
  def loadUserProgress(): List[CampaignLevel] =
    loadFile(userProgressFileName + jsonExtension) match {
      case Some(text) => Try(text.parseJson.convertTo[List[CampaignLevel]]) match {
                            case Success(value) => value
                            case Failure(_) => Logger.log("Error: failed user progress convertion to json")
                                                       deleteFile(Paths.get(userProgressFileName + jsonExtension))
                                                       loadUserProgress()
                          }
      case _ => saveUserProgress(SinglePlayerLevels.getCampaignLevels)
        loadUserProgress()
    }
}
