package it.unibo.osmos.redux.mvc.controller
import it.unibo.osmos.redux.ecs.engine.GameEngine
import it.unibo.osmos.redux.mvc.model.{CampaignLevels, Level}
import it.unibo.osmos.redux.mvc.view.levels.{LevelContext, LevelContextType}
import spray.json._

import scala.io.Source
import scala.util.Try

/**
  * Controller base trait
  */
trait Controller {
  def initLevel(levelContext: LevelContext,
                chosenLevel:Int,
                levelContextType: LevelContextType.Value)
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
                         levelContextType: LevelContextType.Value): Unit = {
    val isSimulation: Boolean = levelContextType eq LevelContextType.simulation
    val loadedLevel = FileManager.loadResource(isSimulation, chosenLevel).get
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
