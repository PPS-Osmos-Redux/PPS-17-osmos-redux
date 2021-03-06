package it.unibo.osmos.redux.mvc.controller.manager.files

import java.io.File
import java.nio.file.Paths

import it.unibo.osmos.redux.mvc.controller.levels.SinglePlayerLevels
import it.unibo.osmos.redux.mvc.controller.levels.structure.CampaignLevel
import it.unibo.osmos.redux.mvc.model.JsonProtocols._
import it.unibo.osmos.redux.utils.Constants.UserHomePaths._
import it.unibo.osmos.redux.utils.Logger
import spray.json.DefaultJsonProtocol._
import spray.json._

import scala.util.{Failure, Success, Try}

/** Defines operation on user progress files. */
object UserProgressFileManager extends FileManager {
  override implicit val who: String = "UserProgressFileManager"

  /** Saves user progress.
    *
    * @param userProgress List[CampaignLevel]
    * @return true if the function is completed with success
    */
  def saveUserProgress(userProgress: List[CampaignLevel]): Boolean = {
    val upFile = new File(DefaultFS.getPath(UserProgressFileName + jsonExtension).toUri)
    createDirectoriesTree(upFile)
    saveToFile(upFile, userProgress.toJson.prettyPrint)
  }

  /** Loads user progress from file.
    *
    * @return List[CampaignLevel]
    */
  def loadUserProgress(): List[CampaignLevel] =
    loadFile(UserProgressFileName + jsonExtension) match {
      case Some(text) => Try(text.parseJson.convertTo[List[CampaignLevel]]) match {
        case Success(value) => value
        case Failure(_) => Logger.log("Error: failed user progress convertion to json")
          deleteFile(Paths.get(UserProgressFileName + jsonExtension))
          loadUserProgress()
      }
      case _ => saveUserProgress(SinglePlayerLevels.getCampaignLevels)
        loadUserProgress()
    }

  /** Delete user progress file
    *
    * @return Try[Unit]
    */
  def deleteUserProgress(): Try[Unit] = deleteFile(DefaultFS.getPath(UserProgressFileName + jsonExtension).toAbsolutePath)
}
