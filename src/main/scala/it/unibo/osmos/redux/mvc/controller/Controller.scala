package it.unibo.osmos.redux.mvc.controller
import it.unibo.osmos.redux.ecs.engine.GameEngine
import it.unibo.osmos.redux.mvc.model.SinglePlayerLevels
import it.unibo.osmos.redux.mvc.view.levels.LevelContext

/**
  * Controller base trait
  */
trait Controller {
  def initLevel(levelContext: LevelContext, chosenLevel:Int, isSimulation:Boolean)
  def startLevel()
  def stopLevel()
  def pauseLevel()
  def resumeLevel()
  def getSinglePlayerLevels:List[(Int,Boolean)] = SinglePlayerLevels.levels.toList
  def getCustomLevels:List[String]
  def initCustomLevel(levelContext: LevelContext, chosenLevel:String, isSimulation:Boolean)
}

case class ControllerImpl() extends Controller {
  var engine:Option[GameEngine] = None

  override def initLevel(levelContext: LevelContext,
                          chosenLevel:Int,
                          isSimulation:Boolean): Unit = {

    val loadedLevel = FileManager.loadResource(isSimulation, chosenLevel).get
    if (isSimulation) loadedLevel.isSimulation = true
    if(engine.isEmpty) engine = Some(GameEngine())
    engine.get.init(loadedLevel, levelContext)
    levelContext.setupLevel(loadedLevel.levelMap.mapShape)
  }

  override def initCustomLevel(levelContext: LevelContext,
                               chosenLevel:String,
                               isSimulation:Boolean): Unit = {
    val loadedLevel = FileManager.loadCustomLevel(chosenLevel)
    if(loadedLevel.isDefined) {
      if (isSimulation) loadedLevel.get.isSimulation = true
      if(engine.isEmpty) engine = Some(GameEngine())
      engine.get.init(loadedLevel.get, levelContext)
      levelContext.setupLevel(loadedLevel.get.levelMap.mapShape)
    } else {
      println("File ", chosenLevel, " not found")
    }
  }

  override def startLevel(): Unit = if (engine.isDefined) engine.get.start()

  override def stopLevel(): Unit = if (engine.isDefined) engine.get.stop()

  override def pauseLevel(): Unit = if (engine.isDefined) engine.get.pause()

  override def resumeLevel(): Unit = if (engine.isDefined) engine.get.resume()

  override def getCustomLevels: List[String] = FileManager.customLevelsFilesName
}
