package it.unibo.osmos.redux.mvc.view.stages

import it.unibo.osmos.redux.multiplayer.common.ActorSystemHolder
import it.unibo.osmos.redux.mvc.view.ViewConstants.Window._
import it.unibo.osmos.redux.mvc.view.scenes._
import scalafx.application.JFXApp

/** Primary stage which holds and manages all the different game scenes */
trait OsmosReduxPrimaryStage extends JFXApp.PrimaryStage

/** Companion object */
object OsmosReduxPrimaryStage {
  /** Creates a new OsmosReduxPrimaryStage, the application root stage
    *
    * @param listener     the PrimaryStageListener
    * @param windowWidth  the window width (which is equal to the screen width by default)
    * @param windowHeight the window width (which is equal to the screen width by default)
    * @return an OsmosReduxPrimaryStageImpl instance
    */
  def apply(listener: PrimaryStageListener,
            windowWidth: Double = DefaultWindowWidth,
            windowHeight: Double = DefaultWindowHeight): OsmosReduxPrimaryStageImpl = new OsmosReduxPrimaryStageImpl(listener, windowWidth, windowHeight)

  /** Primary stage implementation
    *
    * @param listener     the primary stage listener
    * @param windowWidth  the window width
    * @param windowHeight the window height
    */
  class OsmosReduxPrimaryStageImpl(private val listener: PrimaryStageListener, private val windowWidth: Double, private val windowHeight: Double) extends OsmosReduxPrimaryStage
    with MainSceneListener {

    title = DefaultWindowTitle
    resizable = false
    width = windowWidth
    height = windowHeight

    private val mainScene = new MainScene(this, this)

    /** The scene field represents the scene currently shown to the screen */
    scene = mainScene

    override def onPlayClick(): Unit = scene = new LevelSelectionScene(this, listener, mainScene)

    override def onMultiPlayerClick(): Unit = scene = new MultiPlayerScene(this, listener, mainScene)

    override def onEditorClick(): Unit = scene = new EditorLevelSelectionScene(this, listener, mainScene)

    override def onStatsClick(): Unit = scene = new StatsScene(this, listener, mainScene)

    override def onSettingsClick(): Unit = scene = new SettingsScene(this, listener, mainScene)

    /** Stopping the game when the user closes the window */
    onCloseRequest = _ => {
      ActorSystemHolder.kill()
      System.exit(0)
    }
  }

}

/** Listener that manages all the events managed by the primary scene */
trait PrimaryStageListener extends LevelSelectionSceneListener with EditorLevelSelectionSceneListener
  with MultiPlayerSceneListener with SettingsSceneListener with StatsSceneListener
