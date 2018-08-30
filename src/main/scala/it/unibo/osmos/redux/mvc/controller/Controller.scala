package it.unibo.osmos.redux.mvc.controller
import it.unibo.osmos.redux.ecs.engine.GameEngine
import it.unibo.osmos.redux.mvc.model.SinglePlayerLevels.LevelInfo
import it.unibo.osmos.redux.mvc.model.{Level, MultiPlayerLevels, SinglePlayerLevels, SoundsType}
import it.unibo.osmos.redux.mvc.view.events._
import it.unibo.osmos.redux.mvc.view.levels.{GameStateHolder, LevelContext}

/**
  * Controller base trait
  */
trait Controller {
  //TODO: choosen level must be a string and i need to know if the level is custom
  def initLevel(levelContext: LevelContext, chosenLevel:Int/*chosenLevel:String*/ ,isSimulation:Boolean/*, isCustomLevel:Boolean*/)
  def startLevel()
  def stopLevel()
  def pauseLevel()
  def resumeLevel()
  def saveNewCustomLevel(customLevel:Level):Boolean
  def getSoundPath(soundType: SoundsType.Value): Option[String]
  def getSinglePlayerLevels:List[LevelInfo] = SinglePlayerLevels.getLevels
  def getMultiPlayerLevels:List[String] = MultiPlayerLevels.getLevels
  def getCustomLevels:List[String] = FileManager.customLevelsFilesName
}

case class ControllerImpl() extends Controller with GameStateHolder {
  var lastLoadedLevel:Option[String] = None
  var engine:Option[GameEngine] = None

  override def initLevel(levelContext: LevelContext,
                         chosenLevel:Int/*chosenLevel:String*/,
                         isSimulation:Boolean,
                         /*isCustomLevel:Boolean*/): Unit = {

    var loadedLevel:Option[Level] = None
    //if (isCustomLevel) loadedLevel = FileManager.loadCustomLevel(chosenLevel)
    //else {
      loadedLevel = FileManager.loadResource(chosenLevel.toString).toOption
      lastLoadedLevel = Some(chosenLevel.toString)
    //}

    if(loadedLevel.isDefined) {
      if (isSimulation) loadedLevel.get.isSimulation = true
      if(engine.isEmpty) engine = Some(GameEngine())
      //TODO: engine must need a GameStateHolder for the EndGameSystem
      engine.get.init(loadedLevel.get, levelContext/*, this*/)
      levelContext.setupLevel(loadedLevel.get.levelMap.mapShape)
    } else {
      println("Level ", chosenLevel, " not found! is a custom level? "/*, isCustomLevel*/)
    }
  }

  override def startLevel(): Unit = if (engine.isDefined) engine.get.start()

  override def stopLevel(): Unit = if (engine.isDefined) engine.get.stop()

  override def pauseLevel(): Unit = if (engine.isDefined) engine.get.pause()

  override def resumeLevel(): Unit = if (engine.isDefined) engine.get.resume()

  override def saveNewCustomLevel(customLevel: Level): Boolean =
    FileManager.saveLevel(customLevel, customLevel.levelId).isDefined

   /**
    * A generic definition of the game state
    *
    * @return a GameStateEventWrapper
    *///TODO useless for controller
  override def gameCurrentState: GameStateEventWrapper = ???

  override def getSoundPath(soundType: SoundsType.Value): Option[String] = soundType match {
    case SoundsType.menu => Some(FileManager.loadMenuMusic())
    case SoundsType.level => Some(FileManager.loadLevelMusic())
    case SoundsType.button => Some(FileManager.loadButtonsSound())
    case _ => println("Sound type not managed!! [Controller getSoundPath]")
              None
  }

  /**
    * Called on a event T type
    *
    * @param event the event
    */
  override def notify(event: GameStateEventWrapper): Unit = {
    if(lastLoadedLevel.isDefined) {
      SinglePlayerLevels.newEndGameEvent(event, lastLoadedLevel.get)
      FileManager.saveUserProgress(SinglePlayerLevels.userStatistics())
    }
  }
}
