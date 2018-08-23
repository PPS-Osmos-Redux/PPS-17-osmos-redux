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
    with MainSceneListener {

    title = defaultWindowTitle
    fullScreen = fullScreenEnabled
    width = windowWidth
    height = windowHeight

    private val mainScene = new MainScene(this, this)

    /**
      * The scene field represents the scene currently shown to the screen
      */
    scene = mainScene

    override def onPlayClick(): Unit = scene = new LevelSelectionScene(this, listener)

    override def onMultiPlayerClick(): Unit = scene = new MultiPlayerScene(this, listener, mainScene)

    override def onEditorClick(): Unit = scene = new EditorLevelSelectionScene(this, listener)

    /* Stopping the game when the user closes the window */
    onCloseRequest = _ => System.exit(0)

  }
}

/**
  * Listener that manages all the events managed by the primary scene
  */
trait PrimaryStageListener extends LevelSelectionSceneListener with MultiPlayerSceneListener {

}
