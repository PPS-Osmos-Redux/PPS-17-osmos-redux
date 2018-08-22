package it.unibo.osmos.redux.mvc.controller

import java.io.{File, PrintWriter}
import java.nio.file.{FileSystem, FileSystems, Files, Path}

import it.unibo.osmos.redux.ecs.components._
import it.unibo.osmos.redux.ecs.entities._
import it.unibo.osmos.redux.mvc.model.MapShape.{Circle, Rectangle}
import it.unibo.osmos.redux.mvc.model.UserProgress.UserStat
import it.unibo.osmos.redux.mvc.model._
import it.unibo.osmos.redux.utils.Point
import spray.json._

import scala.io.{BufferedSource, Source}
import scala.util.Try

object FileManager {
  val separator: String = "/"
  val levelStartPath: String = separator + "levels" + separator
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
    * @param isSimulation if i have to load a simulation or a playable levels
    * @param chosenLevel  levels id
    * @return content of file wrapped into a Try
    */
  def loadResource(isSimulation: Boolean, chosenLevel: Int): Try[Level] =
    Try(textToLevel(Source.fromInputStream(
      getClass.getResourceAsStream(levelStartPath + chosenLevel + jsonExtension)
    ).mkString).get)

  /**
    * Save a level on file
    * @param level the level to save
    * @param fileName the file name
    * @return option with the file path if it doesn't fail
    */
  def saveLevel(level: Level, fileName: String): Option[Path] = {
    //Check if file exists
    var flag: Boolean = true
    var index: Int = 1
    var path: Path = defaultFS.getPath(levelsDirectory + fileName + jsonExtension)
    do {
      if (Files.exists(path)) {
        //if exists i will try with a new file name
        path = defaultFS.getPath(levelsDirectory + fileName + index + jsonExtension)
        index += 1
      } else {
        flag = false
      }
    } while (flag)
    val levelFile = new File(path.toUri)
    createDirectoriesTree(levelFile)
    import it.unibo.osmos.redux.mvc.model.JsonProtocols.levelFormatter
    if (saveToFile(levelFile, level.toJson.prettyPrint)) Some(path) else None
  }

  def saveUserProgress(userProgress: UserStat): Option[Path] = {
    val path: Path = defaultFS.getPath(userProgressDirectory + userProgressFileName + jsonExtension)
    val upFile = new File(path.toUri)
    createDirectoriesTree(upFile)
    import it.unibo.osmos.redux.mvc.model.JsonProtocols._
    if (saveToFile(upFile, userProgress.toJson.prettyPrint)) Some(path) else None
  }

  def loadUserProgress(): Option[UserStat] = {
    import it.unibo.osmos.redux.mvc.model.JsonProtocols.userProgressFormatter
    loadFile(userProgressDirectory + userProgressFileName + jsonExtension) match {
      case Some(text) => Option(text.parseJson.convertTo[UserStat])
      case _ => None
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
    import it.unibo.osmos.redux.mvc.model.JsonProtocols.levelFormatter
    Try(text.parseJson.convertTo[Level])
  }

  def customLevelsFilesName:List[String] =
    new File(levelsDirectory).listFiles((d, name) => name.endsWith(jsonExtension))
                             .map(f => f.getName.substring(0,f.getName.length-jsonExtension.length))
                             .toList
}
