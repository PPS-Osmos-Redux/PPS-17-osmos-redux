package it.unibo.osmos.redux.mvc.controller
import it.unibo.osmos.redux.ecs.engine.GameEngine
import it.unibo.osmos.redux.mvc.model.{CampaignLevels, Level}
import it.unibo.osmos.redux.mvc.view.levels.LevelContext
import spray.json._

import scala.io.Source
import scala.util.Try

/**
  * Controller base trait
  */
trait Controller {
  def initLevel(levelContext: LevelContext,
                 chosenLevel:Int,
                 isSimulation:Boolean)
  def startLevel()
  def stopLevel()
  def pauseLevel()
  def resumeLevel()
  def getCampaignLevels:List[(Int,Boolean)] = CampaignLevels.levels.toList
}

case class ControllerImpl() extends Controller {
  var engine:Option[GameEngine] = None

  override def initLevel(levelContext: LevelContext,
                          chosenLevel:Int,
                          isSimulation:Boolean): Unit = {

    val text:Try[String] = FileManager.loadResource(isSimulation, chosenLevel)
    import it.unibo.osmos.redux.mvc.model.JsonProtocols._
    val loadedLevel = text.get.parseJson.convertTo[Level]
    if (isSimulation) loadedLevel.isSimulation = true
    if(engine.isEmpty) engine = Some(GameEngine())
    engine.get.init(loadedLevel, levelContext)
    levelContext.setupLevel(loadedLevel.levelMap.mapShape)
  }

  override def startLevel(): Unit = if (engine.isDefined) engine.get.start()

  override def stopLevel(): Unit = if (engine.isDefined) engine.get.stop()

  override def pauseLevel(): Unit = if (engine.isDefined) engine.get.pause()

  override def resumeLevel(): Unit = if (engine.isDefined) engine.get.resume()
}

object FileManager {
  val separator:String = "/"
  val levelStartPath:String = separator + "levels" + separator
  val jsonExtension = ".json"

  /**
    * Reads a file from the resources folder
    * @param isSimulation if i have to load a simulation or a playable levels
    * @param chosenLevel levels id
    * @return content of file wrapped into a Try
    */
  def loadResource(isSimulation:Boolean, chosenLevel:Int): Try[String] ={
    val fileName = (levelStartPath + chosenLevel + jsonExtension).toLowerCase
    Try(Source.fromInputStream(getClass.getResourceAsStream(fileName)).mkString)
  }
}
