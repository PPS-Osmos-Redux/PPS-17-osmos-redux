package it.unibo.osmos.redux.main.mvc.view.stages

import it.unibo.osmos.redux.main.mvc.view.ViewConstants.Window._
import it.unibo.osmos.redux.main.mvc.view.levels.LevelContext
import it.unibo.osmos.redux.main.mvc.view.scenes.{LevelScene, LevelSceneListener, MainScene, MainSceneListener}
import scalafx.application.JFXApp

/**
  * Primary stage which holds and manages all the different game scenes
  */
trait OsmosReduxPrimaryStage extends JFXApp.PrimaryStage {

}

/**
  * Companion object
  */
object OsmosReduxPrimaryStage {
  def apply(listener: PrimaryStageListener,
            fullScreenEnabled: Boolean = false,
            windowWidth: Double = defaultWindowWidth,
            windowHeight: Double = defaultWindowHeight): OsmosReduxPrimaryStageImpl = new OsmosReduxPrimaryStageImpl(listener, fullScreenEnabled, windowWidth, windowHeight)

  /**
    * Primary stage implementation
    * @param listener the primary stage listener
    * @param fullScreenEnabled true if we want the stage to be shown fullscreen, false otherwise
    * @param windowWidth the window width
    * @param windowHeight the window height
    */
  class OsmosReduxPrimaryStageImpl(val listener: PrimaryStageListener, val fullScreenEnabled: Boolean, val windowWidth: Double, val windowHeight: Double) extends OsmosReduxPrimaryStage
    with MainSceneListener with LevelSceneListener {

    title = defaultWindowTitle
    fullScreen = fullScreenEnabled
    width = windowWidth
    height = windowHeight

    /**
      * The scene field represents the scene currently shown to the screen
      */
    scene = new MainScene(this, this)

    override def onPlayClick(): Unit = {
      /* Creating a new level scene */
      val levelScene = new LevelScene(this, this)
      /* Creating a new LevelContext and setting it to the scene */
      val levelContext = LevelContext(levelScene)
      levelScene.levelContext = levelContext
      /* Changing scene scene */
      scene = levelScene
      /* Notify the view the new context */
      listener.onLevelContextSetup(levelContext)
    }

  }
}

/**
  * Listener that manages all the events managed by the primary scene
  */
trait PrimaryStageListener {

  /**
    * This method called when the level context has been created
    * @param levelContext the new level context
    */
  def onLevelContextSetup(levelContext: LevelContext)
}
