package it.unibo.osmos.redux.mvc.controller.manager.files

import java.io.File
import java.nio.file.{Files, Path}

import spray.json._
import spray.json.DefaultJsonProtocol._
import it.unibo.osmos.redux.mvc.model.JsonProtocols._
import FileManager._
import it.unibo.osmos.redux.mvc.controller.LevelInfo
import it.unibo.osmos.redux.mvc.model.Level
import it.unibo.osmos.redux.utils.Logger

import scala.io.Source
import scala.util.{Failure, Success, Try}

object LevelFileManager {

  implicit val who: String = "LevelFileManager"

  /**
    * Reads a file from the resources folder
    *
    * @param chosenLevel levels id
    * @return content of file wrapped into a Option
    */
  def getLevelFromResource(chosenLevel: String, isMultiPlayer: Boolean = false): Option[Level] = {
    import ResourcesPaths.{multiPlayerLevelsPath, singlePlayerLevelsPath}
    val levelsPath = if (isMultiPlayer) multiPlayerLevelsPath else singlePlayerLevelsPath
    val fileStream = this.getClass.getResourceAsStream(levelsPath + chosenLevel + jsonExtension)
    val fileContent = Source.fromInputStream(fileStream).mkString
    fileStream.close()
    textToLevel(fileContent) match {
      case Success(result) => Some(result)
      case Failure(e: Throwable) => Logger.log(e.printStackTrace().toString); None
    }
  }

  /**
    * Save a custom level into user home
    *
    * @param level the level to save
    * @return true if the operation is completed with success
    */
  def saveCustomLevel(level: Level): Boolean = {
    val levelFile = new File(processFilePath(level.levelInfo.name).toUri)
    createDirectoriesTree(levelFile)
    saveToFile(levelFile, level.toJson.prettyPrint)
  }

  /**
    * Load level from file saved into user home directory
    *
    * @param fileName the name of file
    * @return an option with the required level if it doesn't fail
    */
  def getCustomLevel(implicit fileName: String): Option[Level] =
    loadFile(UserHomePaths.levelsDirectory + fileName + jsonExtension) match {
      case Some(text) => textToLevel(text).toOption
      case _ => None
    }

  /**
    * Read from file the custom levels and if exists return their info
    *
    * @return if exists a list of LevelInfo
    */
  def getCustomLevelsInfo: Try[List[LevelInfo]] = {
    /*Create directory tree for avoid NullPointer exception later*/
    createDirectoriesTree(new File(UserHomePaths.levelsDirectory + "rnd.txt"))
    Try(new File(UserHomePaths.levelsDirectory).listFiles((_, name) => name.endsWith(jsonExtension))
      .map(f => loadLevelInfo(f))
      .filter(optLvl => optLvl.isDefined).map(opt => opt.get).toList)
  }

  private def textToLevel(text: String): Try[Level] = Try(text.parseJson.convertTo[Level])
  private def textToLevelInfo(text:String): Try[LevelInfo] = Try(text.parseJson.convertTo[LevelInfo])

  import UserHomePaths._
  def processFilePath(fileName:String, index:Option[Int] = None): Path = {
    val path = defaultFS.getPath(levelsDirectory + fileName+index.getOrElse("") + jsonExtension)
    if (Files.exists(path)) {
      processFilePath(fileName, if(index.isDefined) Some(index.get+1) else Some(1))
    } else {
      path
    }
  }

  private def loadLevelInfo(implicit fileName: String): Option[LevelInfo] =
    loadFile(UserHomePaths.levelsDirectory + fileName + jsonExtension) match {
      case Some(text) => textToLevelInfo(text).toOption
      case _ => None
    }
}
