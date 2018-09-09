package it.unibo.osmos.redux.mvc.view.stages

import it.unibo.osmos.redux.mvc.controller.manager.files.StyleFileManager
import it.unibo.osmos.redux.mvc.view.ViewConstants.Window._
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
            windowWidth: Double = defaultWindowWidth,
            windowHeight: Double = defaultWindowHeight): OsmosReduxPrimaryStageImpl = new OsmosReduxPrimaryStageImpl(listener, windowWidth, windowHeight)

  /**
    * Primary stage implementation
    *
    * @param listener          the primary stage listener
    * @param windowWidth       the window width
    * @param windowHeight      the window height
    */
  class OsmosReduxPrimaryStageImpl(val listener: PrimaryStageListener, val windowWidth: Double, val windowHeight: Double) extends OsmosReduxPrimaryStage
    with MainSceneListener {

    title = defaultWindowTitle
    resizable = false
    width = windowWidth
    height = windowHeight

    private val mainScene = new MainScene(this, this) {
      // TODO: changing scene will ignore the imported style
      stylesheets.addAll(StyleFileManager.getStyle)
    }

    /**
      * The scene field represents the scene currently shown to the screen
      */
    scene = mainScene

    override def onPlayClick(): Unit = scene = new LevelSelectionScene(this, listener, mainScene)

    override def onMultiPlayerClick(): Unit = scene = new MultiPlayerScene(this, listener, mainScene)

    override def onEditorClick(): Unit = scene = new EditorLevelSelectionScene(this, listener, mainScene)

    override def onStatsClick(): Unit = scene = new StatsScene(this, listener, mainScene)

    override def onControlsClick(): Unit = scene = new ControlsScene(this, listener, mainScene)

    override def onSettingsClick(): Unit = scene = new SettingsScene(this, listener, mainScene)

    /* Stopping the game when the user closes the window */
    onCloseRequest = _ => System.exit(0)
  }

}

/**
  * Listener that manages all the events managed by the primary scene
  */
trait PrimaryStageListener extends LevelSelectionSceneListener with EditorLevelSelectionSceneListener
  with MultiPlayerSceneListener with MultiPlayerLobbySceneListener with MultiPlayerLevelSelectionSceneListener
  with SettingsSceneListener with StatsSceneListener {

}
