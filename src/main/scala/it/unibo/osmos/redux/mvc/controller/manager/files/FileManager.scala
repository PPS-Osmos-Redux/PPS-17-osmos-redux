package it.unibo.osmos.redux.mvc.controller.manager.files

import java.io.{File, PrintWriter}
import java.nio.file._

import it.unibo.osmos.redux.utils.Constants.UserHomePaths
import it.unibo.osmos.redux.utils.Logger

import scala.io.{BufferedSource, Source}
import scala.language.postfixOps
import scala.util.{Failure, Success, Try}

/** Abstract class with basics files management functions*/
abstract class FileManager {
  implicit val who: String
  val jsonExtension = ".json"

  /** Get resource file content as text.
    *
    * @param resourcePath file path referred to resource dir
    * @return String
    */
  protected def getResourcesFileText(resourcePath:String): String = {
    val fileStream =  this.getClass.getResourceAsStream(resourcePath)
    try Source.fromInputStream(fileStream).mkString
    finally  fileStream.close()
  }

  /** Delete file by path.
    *
    * @param filePath Path
    * @return Try[Unit]
    */
  protected def deleteFile(filePath:Path):Try[Unit] = Try(Files.delete(filePath))

  /** Creates directories tree for a new file.
    *
    * @param file File
    * @return true if no Exceptions occurs
    */
  protected def createDirectoriesTree(file:File):Boolean = Try(file.getParentFile.mkdirs()) match {
   case Success(_) =>  true
   case Failure(exception) =>
     Logger.log("Error: SecurityException directories are protected [createDirectoriesTree] " +
       exception.toString)
     false
  }

  /** Create a new file or overwrite its content if exists.
    *
    * @param file File
    * @param text text to write into file
    * @return true if the operation is terminated with success
    */
  protected def saveToFile(file:File, text: String): Boolean = {
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

  /** Get the file content.
    *
    * @param filePath file path to String
    * @return An Option with the text if the operation is terminated with success
    */
  protected def loadFile(filePath:String):Option[String] = {
    val source: Try[BufferedSource] = Try(Source.fromFile(UserHomePaths.DefaultFS.getPath(filePath).toUri))
    if (source.isSuccess) {
      try {
        return Some(source.get.mkString)
      } catch {
        case e:Throwable => Logger.log("Error reading file: " + filePath + " stack trace: " +
          e.printStackTrace().toString)
      }
      finally source.get.close()
    }
    None
  }

  protected implicit def stringPathToFile(path:String): File = new File(path)
}
