package it.unibo.osmos.redux.mvc.controller

import java.io.{File, PrintWriter}
import java.nio.file._

import it.unibo.osmos.redux.mvc.controller.UserHomePaths.userProgressDirectory
import it.unibo.osmos.redux.mvc.model.JsonProtocols._
import it.unibo.osmos.redux.mvc.model._
import it.unibo.osmos.redux.utils.Logger
import spray.json._
import spray.json.DefaultJsonProtocol._
import scala.io.{BufferedSource, Source}
import scala.language.postfixOps
import scala.util.{Failure, Success, Try}

object ResourcesPaths {
  val separator: String = "/"
  val levelStartPath: String = separator + "levels"
  val singlePlayerLevelsPath: String = levelStartPath + separator + "singlePlayer" + separator
  val multiPlayerLevelsPath: String = levelStartPath + separator + "multiPlayer" + separator
}

object UserHomePaths {
  val defaultFS: FileSystem = FileSystems.getDefault
  val systemSeparator: String = defaultFS.getSeparator
  val userHome: String = System.getProperty("user.home")
  val gameDirectory:String = "Osmos-Redux" + systemSeparator
  val levelsDirectory: String = userHome + systemSeparator + gameDirectory +
    "CustomLevels" + systemSeparator
  val userProgressFileName = "UserProgress"
  val userProgressDirectory:String = userHome + systemSeparator + gameDirectory +
    userProgressFileName + systemSeparator
}

object FileManager {
  implicit val who: String = "FileManager"
  val jsonExtension = ".json"

  /**
    * Reads a file from the resources folder
    *
    * @param chosenLevel  levels id
    * @return content of file wrapped into a Option
    */
  def loadResource(chosenLevel: String, isMultiPlayer: Boolean = false): Option[Level] = {
    import ResourcesPaths._
    val levelsPath = if (isMultiPlayer) multiPlayerLevelsPath else singlePlayerLevelsPath
    val fileStream = this.getClass.getResourceAsStream(levelsPath + chosenLevel + jsonExtension)
    val fileContent = Source.fromInputStream(fileStream).mkString
    textToLevel(fileContent) match {
      case Success(result) => Some(result)
      case Failure(e: Throwable) => Logger.log(e.printStackTrace().toString); None
    }
  }

  /**
    * Save a level on file
    * @param level the level to save
    * @return option with the file path if it doesn't fail
    */
  def saveLevel(level: Level): Option[Path] = {
    import UserHomePaths._
    def checkFileExist(fileName:String, index:Option[Int] = None): Path = {
      val path = defaultFS.getPath(levelsDirectory + fileName+index.getOrElse("") + jsonExtension)
      if (Files.exists(path)) {
        checkFileExist(fileName, if(index.isDefined) Some(index.get+1) else Some(1))
      } else {
        path
      }
    }
    val path = checkFileExist(level.levelInfo.name)
    val levelFile = new File(path.toUri)
    createDirectoriesTree(levelFile)
    if (saveToFile(levelFile, level.toJson.prettyPrint)) Some(path) else None
  }

  /**
    * Delete file by name
    * @param fileName file name
    */
  def deleteLevel(fileName:String):Try[Unit] = Try(Files.delete(Paths.get(UserHomePaths.levelsDirectory + fileName + jsonExtension)))


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

  /**
    * Load level from file saved into user home directory
    * @param fileName the name of file
    * @return an option with the required level if it doesn't fail
    */
  def loadCustomLevel(implicit fileName: String): Option[Level] =
    loadFile(UserHomePaths.levelsDirectory + fileName + jsonExtension) match {
      case Some(text) => textToLevel(text).toOption
      case _ => None
  }

  /**
    * Load level from file saved into user home directory
    * @param fileName the name of file
    * @return an option with the required levelInfo if it doesn't fail
    */
  def loadLevelInfo(implicit fileName: String): Option[LevelInfo] =
    loadFile(UserHomePaths.levelsDirectory + fileName + jsonExtension) match {
      case Some(text) => textToLevelInfo(text).toOption
      case _ => None
    }


  def saveToFile(file:File, text: String): Boolean = {
    val writer = new PrintWriter(file)
    try {
      writer.write(text)
      return true
    } catch {
      case e: Throwable => Logger.log("Exception occurred writing on file: " + file.getName + " StackTrace: " +
        e.printStackTrace())
    } finally writer.close()
    false
  }

  def loadFile(filePath:String):Option[String] = {
    val source: Try[BufferedSource] = Try(Source.fromFile(UserHomePaths.defaultFS.getPath(filePath).toUri))
    if (source.isSuccess) {
      try return Some(source.get.mkString)
      catch {
        case e:Throwable => Logger.log("Error reading file: " + filePath + " stack trace: " + e.printStackTrace().toString)
      }
      finally source.get.close()
    }
    None
  }

  /**
    * Creates directories tree
    * @param file File object
    * @return true if no Exceptions occurs
    */
  def createDirectoriesTree(file:File):Boolean = Try(file.getParentFile.mkdirs()) match {
   case Success(_) =>  true
   case Failure(exception) =>Logger.log("Error: SecurityException directories are protected [createDirectoriesTree]"
                                    + exception.getMessage)
                             false
  }

  /**
    * Convert a json string into a Level
    * @param text json string
    * @return Try with Level if it doesn't fail
    */
  def textToLevel(text: String): Try[Level] = Try(text.parseJson.convertTo[Level])

  /**
    * Convert a json string into a LevelInfo
    * @param text json string
    * @return Try with Level if it doesn't fail
    */
  def textToLevelInfo(text:String): Try[LevelInfo] = Try(text.parseJson.convertTo[LevelInfo])

  /**
    * Return file name without json extension
    * @param file file object
    * @return file name
    */
  implicit def getFileNameWithoutJsonExtension(file:File):String = file.getName.substring(0,file.getName.length-jsonExtension.length)

  /**
    * Read from file the custom levels and if exists return their info
    * @return if exists a list of LevelInfo
    */
  def customLevelsFilesName:Try[List[LevelInfo]] = {
    /*Create directory tree for avoid NullPointer exception later*/
    createDirectoriesTree(new File(UserHomePaths.levelsDirectory + "rnd.txt"))
    Try(new File(UserHomePaths.levelsDirectory).listFiles((_, name) => name.endsWith(jsonExtension))
      .map(f => loadLevelInfo(f))
      .filter(optLvl => optLvl.isDefined).map(opt => opt.get).toList)
  }

  def getStyle: String = {
    try {
      val url = getClass.getResource("/style/style.css")
      //println("style url: " + url)
      url.toString
    } catch {
      case _: NullPointerException =>
        Logger.log("Error: style.css file not found")
        throw new NullPointerException("style.css file not found");
    }
  }

  val soundsPath: String = ResourcesPaths.separator + "sounds" + ResourcesPaths.separator
  /**
    * Gets menu music path
    * @return menu music string path
    */
  def loadMenuMusic(): String = getClass.getResource(soundsPath + "MenuMusic.mp3").toURI toString

  /**
    * Gets button sound path
    * @return button sound string path
    */
  def loadButtonsSound(): String = getClass.getResource(soundsPath + "ButtonSound.mp3").toURI toString

  /**
    * Gets level music path
    * @return level music string path
    */
  def loadLevelMusic(): String = getClass.getResource(soundsPath + "LevelMusic.mp3").toURI toString

}
