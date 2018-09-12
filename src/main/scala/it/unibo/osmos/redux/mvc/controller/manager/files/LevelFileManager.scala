package it.unibo.osmos.redux.mvc.controller.manager.files

import java.io.File
import java.nio.file.{Files, Path, Paths}

import it.unibo.osmos.redux.mvc.controller.levels.structure.{Level, LevelInfo}
import it.unibo.osmos.redux.mvc.model.JsonProtocols._
import it.unibo.osmos.redux.utils.Constants.UserHomePaths._
import it.unibo.osmos.redux.utils.Logger
import spray.json.DefaultJsonProtocol._
import spray.json._

import scala.util.{Failure, Success, Try}

/** Defines operation on levels files */
object LevelFileManager extends FileManager {

  implicit val who: String = "LevelFileManager"

  /** Get a list of file names who contain single player or multiPlayer levels
    *
    * @param multiPlayer true if is required multiPlayer level files name
    * @return an Option of List[String]
    */
  def getLevelsConfigResourcesPath(multiPlayer: Boolean = false): Option[List[String]] = {
    import it.unibo.osmos.redux.utils.Constants.ResourcesPaths._
    val configPath = (if (multiPlayer) ConfigMultiPlayer else ConfigSinglePlayer) + jsonExtension
    Try(getResourcesFileText(configPath).parseJson.convertTo[List[String]]).toOption
  }

  /** Reads a file from the resources folder.
    *
    * @param chosenLevel levels name
    * @return content of file as Level wrapped into an Option
    */
  def getLevelFromResource(chosenLevel: String, isMultiPlayer: Boolean = false): Option[Level] = {
    import it.unibo.osmos.redux.utils.Constants.ResourcesPaths._
    val levelsPath = (if (isMultiPlayer) MultiPlayerLevelsPath else SinglePlayerLevelsPath) + chosenLevel + jsonExtension
    textToLevel(getResourcesFileText(levelsPath)) match {
      case Success(level) => level.checkCellPosition()
        Some(level)
      case Failure(e: Throwable) => Logger.log(e.printStackTrace().toString); None
    }
  }

  private def textToLevel(text: String): Try[Level] = Try(text.parseJson.convertTo[Level])

  /** Save a custom level into user home.
    *
    * @param level Level
    * @return true if the operation is completed with success
    */
  def saveCustomLevel(level: Level): Boolean = {
    val filePath = processFilePath(level.levelInfo.name)
    val levelFile = new File(filePath._1.toUri)
    level.levelInfo.name = filePath._2
    createDirectoriesTree(levelFile)
    saveToFile(levelFile, level.toJson.prettyPrint)
  }

  /** Delete a custom level file.
    *
    * @param levelName the name of the file
    * @return Try[Unit]
    */
  def deleteCustomLevel(levelName: String): Try[Unit] = deleteFile(Paths.get(LevelsDirectory + levelName + jsonExtension))

  /** Load level from file saved into user home directory
    *
    * @param fileName the name of the file
    * @return an Option with the required Level if it doesn't fail
    */
  def getCustomLevel(implicit fileName: String): Option[Level] =
    loadFile(LevelsDirectory + fileName + jsonExtension) match {
      case Some(text) => textToLevel(text) match {
        case Success(level) => level.checkCellPosition()
          Some(level)
        case Failure(_) => Logger.log("Error: convertion of custom level " + fileName + " is failed")(who)
          None
      }
      case _ => None
    }

  /** Read from file the custom levels info
    *
    * @return if exists a list of LevelInfo
    */
  def getCustomLevelsInfo: Try[List[LevelInfo]] = {
    /*Create directory tree for avoid NullPointer exception later*/
    createDirectoriesTree(new File(LevelsDirectory + "rnd.txt"))
    Try(new File(LevelsDirectory).listFiles((_, name) => name.endsWith(jsonExtension))
      .map(f => loadLevelInfo(f))
      .filter(optLvl => optLvl.isDefined).map(opt => opt.get).toList)
  }

  private def loadLevelInfo(implicit fileName: String): Option[LevelInfo] =
    loadFile(LevelsDirectory + fileName + jsonExtension) match {
      case Some(text) => textToLevelInfo(text).toOption
      case _ => None
    }

  private def textToLevelInfo(text: String): Try[LevelInfo] = Try(text.parseJson.convertTo[LevelInfo])

  /** Read from file into resources the levels info
    *
    * @return Option[LevelInfo]
    */
  def getResourceLevelInfo(filePath: String): Option[LevelInfo] = {
    Try(getResourcesFileText(filePath).parseJson.convertTo[LevelInfo]) match {
      case Success(value) => Some(value)
      case Failure(_) => Logger.log("File not found into resources: " + filePath)
        None
    }
  }

  private def processFilePath(fileName: String, index: Option[Int] = None): (Path, String) = {
    val uniqueName = fileName + index.getOrElse("")
    val path = DefaultFS.getPath(LevelsDirectory + uniqueName + jsonExtension)
    if (Files.exists(path)) {
      processFilePath(fileName, if (index.isDefined) Some(index.get + 1) else Some(1))
    } else {
      (path, uniqueName)
    }
  }

  private implicit def getFileNameWithoutJsonExtension(file: File): String = file.getName.substring(0, file.getName.length - jsonExtension.length)
}
