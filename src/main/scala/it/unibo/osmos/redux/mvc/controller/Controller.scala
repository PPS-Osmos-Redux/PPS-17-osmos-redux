package it.unibo.osmos.redux.mvc.controller
import it.unibo.osmos.redux.ecs.engine.GameEngine
import it.unibo.osmos.redux.mvc.model.{Level, MultiPlayerLevels, SinglePlayerLevels}
import it.unibo.osmos.redux.mvc.view.events._
import it.unibo.osmos.redux.mvc.view.levels.{GameStateHolder, LevelContext}

trait Observable {
  def subscribe(observer:Observer)
}

trait Observer {
  def notify(event: GameStateEventWrapper, levelContext: GameStateHolder)
}

/**
  * Controller base trait
  */
trait Controller {
  def initLevel(levelContext: LevelContext, chosenLevel:String, isSimulation:Boolean, isCustomLevel:Boolean)
  def startLevel()
  def stopLevel()
  def pauseLevel()
  def resumeLevel()
  def saveNewCustomLevel(customLevel:Level):Boolean
  def getSinglePlayerLevels:List[(String,Boolean)] = SinglePlayerLevels.getLevels
  def getMultiPlayerLevels:List[String] = MultiPlayerLevels.getLevels
  def getCustomLevels:List[String] = FileManager.customLevelsFilesName
}

case class ControllerImpl() extends Controller with Observer {
  var engine:Option[GameEngine] = None

  override def initLevel(levelContext: LevelContext,
                         chosenLevel:String,
                         isSimulation:Boolean,
                         isCustomLevel:Boolean): Unit = {

    var loadedLevel:Option[Level] = None
    if (isCustomLevel) loadedLevel = FileManager.loadCustomLevel(chosenLevel)
    else loadedLevel = FileManager.loadResource(chosenLevel).toOption

    if(loadedLevel.isDefined) {
      if (isSimulation) loadedLevel.get.isSimulation = true
      if(engine.isEmpty) engine = Some(GameEngine())
      engine.get.init(loadedLevel.get, levelContext, this)
      levelContext.setupLevel(loadedLevel.get.levelMap.mapShape)
    } else {
      println("File ", chosenLevel, " not found! is a custom level? ", isCustomLevel)
    }
  }

  override def startLevel(): Unit = if (engine.isDefined) engine.get.start()

  override def stopLevel(): Unit = if (engine.isDefined) engine.get.stop()

  override def pauseLevel(): Unit = if (engine.isDefined) engine.get.pause()

  override def resumeLevel(): Unit = if (engine.isDefined) engine.get.resume()

  override def notify(event: GameStateEventWrapper, levelContext: GameStateHolder): Unit = {
    if(event.equals(GameWon)) {
      SinglePlayerLevels.unlockNextLevel()
      FileManager.saveUserProgress(SinglePlayerLevels.toUserProgression)
    }
    levelContext.notify(event)
  }

  override def saveNewCustomLevel(customLevel: Level): Boolean =
    FileManager.saveLevel(customLevel, customLevel.levelId).isDefined
}
