package it.unibo.osmos.redux.mvc.view.stages

import it.unibo.osmos.redux.mvc.view.ViewConstants.Window._
import it.unibo.osmos.redux.mvc.view.levels.{LevelContext, LevelContextType}
import it.unibo.osmos.redux.mvc.view.scenes._
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
    with MainSceneListener with LevelSelectionSceneListener with MultiPlayerSceneListener {

    title = defaultWindowTitle
    fullScreen = fullScreenEnabled
    width = windowWidth
    height = windowHeight

    /**
      * The scene field represents the scene currently shown to the screen
      */
    scene = new MainScene(this, this)

    override def onPlayClick(): Unit = scene = new LevelSelectionScene(this, this)

    override def onMultiPlayerClick(): Unit = scene = new MultiPlayerScene(this, this)

    override def onLevelContextCreated(levelContext: LevelContext, level: Int, levelContextType: LevelContextType.Value): Unit = listener.onLevelContextCreated(levelContext, level, levelContextType)

    override def onStartLevel(): Unit = listener.onStartLevel()

    override def onPauseLevel(): Unit = listener.onPauseLevel()

    override def onResumeLevel(): Unit = listener.onResumeLevel()

    override def onStopLevel(): Unit = listener.onStopLevel()


    /* Stopping the game when the user closes the window */
    onCloseRequest = _ => System.exit(0)

  }
}

/**
  * Listener that manages all the events managed by the primary scene
  */
trait PrimaryStageListener extends LevelSelectionSceneListener {

}
