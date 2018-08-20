package it.unibo.osmos.redux.mvc.controller

import java.io.{File, PrintWriter}
import java.nio.file.{FileSystem, FileSystems, Files, Path}

import it.unibo.osmos.redux.mvc.model._
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
  val levelsDirectory: String = userHome + systemSeparator + "Osmos-Redux" +
    systemSeparator + "CustomLevels" + systemSeparator

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
    if (Try(levelFile.getParentFile.mkdirs()).isFailure) {
      println("Error: SecurityException directories are protected")
      return None
    }
    val writer = new PrintWriter(levelFile)
    import it.unibo.osmos.redux.mvc.model.JsonProtocols.levelFormatter
    try writer.write(level.toJson.prettyPrint)
    catch {
      case e: Throwable => println("Exception occurred writing on file: ",e.printStackTrace())
                           return None
    } finally writer.close()
    Some(path)
  }

  /**
    * Load level from file saved into user home directory
    * @param fileName the name of file
    * @return an option with the required level if it doesn't fail
    */
  def loadCustomLevel(fileName: String): Option[Level] = {
    val source: Try[BufferedSource] = Try(Source.fromFile(defaultFS.getPath(levelsDirectory + fileName + jsonExtension).toUri))
    if (source.isSuccess) {
      val lines = try source.get.mkString
                         catch {
                           case e:Throwable => println("Error reading custom level file: ", e)
                                               return None
                         }
                         finally source.get.close()
      val tryLevel = textToLevel(lines)
      if (tryLevel.isSuccess) return Some(tryLevel.get)
    }
    None
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
}
