package it.unibo.osmos.redux.mvc.controller.manager.files

import it.unibo.osmos.redux.mvc.model.JsonProtocols._
import it.unibo.osmos.redux.mvc.model.{Setting, Volume}
import it.unibo.osmos.redux.utils.Constants.UserHomePaths.SettingFilePath
import spray.json.DefaultJsonProtocol._
import spray.json._

object SettingsFileManger extends FileManager {
  override implicit val who: String = "SettingsFileManger"

  /**
    * Save setting on file
    * @param settings List[Setting]
    */
  def saveSettings(settings:List[Setting]): Unit = {
    createDirectoriesTree(SettingFilePath)
    saveToFile(SettingFilePath + jsonExtension, settings.toJson.prettyPrint)
  }

  /**
    * Load settings from file
    * @return List[Setting]
    */
  def loadSettings():List[Setting] = loadFile(SettingFilePath + jsonExtension) match {
    case Some(settings) => settings.parseJson.convertTo[List[Setting]]
    case None => List()
  }
}
