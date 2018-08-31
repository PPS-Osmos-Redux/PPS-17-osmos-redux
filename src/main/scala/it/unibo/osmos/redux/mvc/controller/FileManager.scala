package it.unibo.osmos.redux.mvc.controller

import java.io.{File, PrintWriter}
import java.nio.file._

import it.unibo.osmos.redux.mvc.model.JsonProtocols._
import it.unibo.osmos.redux.mvc.model.SinglePlayerLevels.{LevelInfo, UserStat}
import it.unibo.osmos.redux.mvc.model._
import spray.json._

import scala.io.{BufferedSource, Source}
import scala.language.postfixOps
import scala.util.{Failure, Success, Try}

object FileManager {
  val separator: String = "/"
  val levelStartPath: String = separator + "levels"
  val singlePlayerLevelsPath: String = levelStartPath + separator + "singlePlayer" + separator
  val multiPlayerLevelsPath: String = levelStartPath + separator + "multiPlayer" + separator
  val jsonExtension = ".json"

  val defaultFS: FileSystem = FileSystems.getDefault
  val systemSeparator: String = defaultFS.getSeparator
  val userHome: String = System.getProperty("user.home")
  val gameDirectory:String = "Osmos-Redux" + systemSeparator
  val levelsDirectory: String = userHome + systemSeparator + gameDirectory +
    "CustomLevels" + systemSeparator
  val userProgressFileName = "UserProgress"
  val userProgressDirectory:String = userHome + systemSeparator + gameDirectory +
    userProgressFileName + systemSeparator

  /**
    * Reads a file from the resources folder
    *
    * @param chosenLevel  levels id
    * @return content of file wrapped into a Option
    */
  def loadResource(chosenLevel: String, isMultiPlayer: Boolean = false): Option[Level] = {
    val levelsPath = if (isMultiPlayer) multiPlayerLevelsPath else singlePlayerLevelsPath
    val fileStream = getClass.getResourceAsStream(levelsPath + chosenLevel + jsonExtension)
    val fileContent = Source.fromInputStream(fileStream).mkString
    textToLevel(fileContent) match {
      case Success(result) => Some(result)
      case Failure(e: Throwable) => e.printStackTrace(); None
    }
  }

  /**
    * Save a level on file
    * @param level the level to save
    * @return option with the file path if it doesn't fail
    */
  def saveLevel(level: Level): Option[Path] = {
    def checkFileExist(fileName:String, index:Option[Int] = None): Path = {
      val path = defaultFS.getPath(levelsDirectory + fileName+index.getOrElse("") + jsonExtension)
      if (Files.exists(path)) {
        checkFileExist(fileName, if(index.isDefined) Some(index.get+1) else Some(1))
      } else {
        path
      }
    }
    val path = checkFileExist(level.levelId)
    val levelFile = new File(path.toUri)
    createDirectoriesTree(levelFile)
    if (saveToFile(levelFile, level.toJson.prettyPrint)) Some(path) else None
  }

  /**
    * Delete file by name
    * @param fileName file name
    */
  def deleteLevel(fileName:String):Try[Unit] = Try(Files.delete(Paths.get(levelsDirectory + fileName + jsonExtension)))


  /**
    * Saves user progress
    * @param userProgress current user progress
    * @return Option with file path of user progress file
    */
  def saveUserProgress(userProgress: UserStat): Option[Path] = {
    val path: Path = defaultFS.getPath(userProgressDirectory + userProgressFileName + jsonExtension)
    val upFile = new File(path.toUri)
    createDirectoriesTree(upFile)
    if (saveToFile(upFile, userProgress.toJson.prettyPrint)) Some(path) else None
  }

  /**
    * Loads user progress from file
    * @return UserStat
    */
  def loadUserProgress(): UserStat = {
    loadFile(userProgressDirectory + userProgressFileName + jsonExtension) match {
      case Some(text) => text.parseJson.convertTo[UserStat]
      case _ => saveUserProgress(SinglePlayerLevels.userStatistics())
                loadUserProgress()
    }
  }

  /**
    * Load level from file saved into user home directory
    * @param fileName the name of file
    * @return an option with the required level if it doesn't fail
    */
  def loadCustomLevel(fileName: String): Option[Level] = {
    loadFile(levelsDirectory + fileName + jsonExtension) match {
      case Some(text) => textToLevel(text).toOption
      case _ => None
    }
  }


  def saveToFile(file:File, text: String): Boolean = {
    val writer = new PrintWriter(file)
    try {
      writer.write(text)
      return true
    } catch {
      case e: Throwable => println("Exception occurred writing on file: ", file.getName,
        e.printStackTrace())
    } finally writer.close()
    false
  }

  def loadFile(filePath:String):Option[String] = {
    val source: Try[BufferedSource] = Try(Source.fromFile(defaultFS.getPath(filePath).toUri))
    if (source.isSuccess) {
      try return Some(source.get.mkString)
      catch {
        case e:Throwable => println("Error reading file: ", filePath,e.printStackTrace())
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
  def createDirectoriesTree(file:File):Boolean = {
    if (Try(file.getParentFile.mkdirs()).isFailure) {
      println("Error: SecurityException directories are protected")
      return false
    }
    true
  }

  /**
    * Convert a json string into a Level
    * @param text json string
    * @return Try with Level if it doesn't fail
    */
  def textToLevel(text: String): Try[Level] = {
    Try(text.parseJson.convertTo[Level])
  }

  /**
    * Read from file the custom levels and if exists return their info
    * @return if exists a list of LevelInfo
    */
  def customLevelsFilesName:Try[List[LevelInfo]] =
    Try(new File(levelsDirectory).listFiles((_, name) => name.endsWith(jsonExtension))
                                 .map(f => f.getName.substring(0,f.getName.length-jsonExtension.length))
                                 .map(lvlFileName => loadCustomLevel(lvlFileName)).filter(optLvl => optLvl.isDefined)
                                 .map(lvl => LevelInfo(lvl.get.levelId, lvl.get.victoryRule)).toList)

  def getStyle: String = {
    try {
      val url = getClass.getResource("/style/style.css")
      //println("style url: " + url)
      url.toString
    } catch {
      case _: NullPointerException =>
        println("Error: style.css file not found")
        throw new NullPointerException("style.css file not found");
    }
  }

  val soundsPath: String = separator + "sounds" + separator

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
