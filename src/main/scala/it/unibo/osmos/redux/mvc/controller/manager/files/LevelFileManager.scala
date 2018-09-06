package it.unibo.osmos.redux.mvc.controller.manager.files

import java.io.File
import java.nio.file.{Files, Path, Paths}

import it.unibo.osmos.redux.mvc.controller.levels.structure.{Level, LevelInfo}
import it.unibo.osmos.redux.mvc.model.JsonProtocols._
import it.unibo.osmos.redux.utils.Logger
import spray.json._
import spray.json.DefaultJsonProtocol._
import it.unibo.osmos.redux.utils.Constants.UserHomePaths._
import scala.io.Source
import scala.util.{Failure, Success, Try}

object LevelFileManager extends FileManager {

  implicit val who: String = "LevelFileManager"

  def getLevelsConfigResourcesPath(multiplayer:Boolean = false) : Option[List[String]] = {
    import it.unibo.osmos.redux.utils.Constants.ResourcesPaths._
    val levelsPath = if (multiplayer) configMultiPlayer else configSinglePlayer
    val fileStream =  this.getClass.getResourceAsStream(levelsPath + jsonExtension)
    val fileContent = Source.fromInputStream(fileStream).mkString
    fileStream.close()
    Try(fileContent.parseJson.convertTo[List[String]]).toOption
  }

  /**
    * Reads a file from the resources folder
    *
    * @param chosenLevel levels id
    * @return content of file wrapped into a Option
    */
  def getLevelFromResource(chosenLevel: String, isMultiPlayer: Boolean = false): Option[Level] = {
    import it.unibo.osmos.redux.utils.Constants.ResourcesPaths._
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
    val filePath = processFilePath(level.levelInfo.name)
    val levelFile = new File(filePath._1.toUri)
    level.levelInfo.name = filePath._2
    createDirectoriesTree(levelFile)
    saveToFile(levelFile, level.toJson.prettyPrint)
  }

  /**
    * Delete a custom level file
    * @param levelName the name of the file
    * @return Try[Unit]
    */
  def deleteCustomLevel(levelName:String): Try[Unit] = deleteFile(Paths.get(levelsDirectory + levelName + jsonExtension))

  /**
    * Load level from file saved into user home directory
    *
    * @param fileName the name of file
    * @return an option with the required level if it doesn't fail
    */
  def getCustomLevel(implicit fileName: String): Option[Level] =
    loadFile(levelsDirectory + fileName + jsonExtension) match {
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
    createDirectoriesTree(new File(levelsDirectory + "rnd.txt"))
    Try(new File(levelsDirectory).listFiles((_, name) => name.endsWith(jsonExtension))
      .map(f => loadLevelInfo(f))
      .filter(optLvl => optLvl.isDefined).map(opt => opt.get).toList)
  }

  def getResourceLevelInfo(filePath:String):Option[LevelInfo] = {
    val fileStream =  this.getClass.getResourceAsStream(filePath)
    val fileContent = Source.fromInputStream(fileStream).mkString
    fileStream.close()
    Try(fileContent.parseJson.convertTo[LevelInfo]) match {
      case Success(value) => Some(value)
      case Failure(_) => Logger.log("File not found into resources: " + filePath)
        None
    }
  }

  private def textToLevel(text: String): Try[Level] = Try(text.parseJson.convertTo[Level])
  private def textToLevelInfo(text:String): Try[LevelInfo] = Try(text.parseJson.convertTo[LevelInfo])

  private def processFilePath(fileName:String, index:Option[Int] = None): (Path, String) = {
    val uniqueName =  fileName+index.getOrElse("")
    val path = defaultFS.getPath(levelsDirectory + uniqueName + jsonExtension)
    if (Files.exists(path)) {
      processFilePath(fileName, if(index.isDefined) Some(index.get+1) else Some(1))
    } else {
      (path, uniqueName)
    }
  }

  private def loadLevelInfo(implicit fileName: String): Option[LevelInfo] =
    loadFile(levelsDirectory + fileName + jsonExtension) match {
      case Some(text) => textToLevelInfo(text).toOption
      case _ => None
    }

  /**
    * Return file name without json extension
    * @param file file object
    * @return file name
    */
  private implicit def getFileNameWithoutJsonExtension(file:File):String = file.getName.substring(0,file.getName.length-jsonExtension.length)
}
