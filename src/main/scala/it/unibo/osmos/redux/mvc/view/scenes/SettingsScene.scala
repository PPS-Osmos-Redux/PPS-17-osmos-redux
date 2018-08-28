package it.unibo.osmos.redux.mvc.view.scenes

import it.unibo.osmos.redux.mvc.view.components.menu.{MainMenuBar, MainMenuBarListener}
import it.unibo.osmos.redux.mvc.view.context.LevelContext
import it.unibo.osmos.redux.mvc.view.stages.PrimaryStageListener
import scalafx.geometry.Pos
import scalafx.scene.control.{Button, CheckBox}
import scalafx.scene.layout.VBox
import scalafx.stage.Stage

class SettingsScene(override val parentStage: Stage, listener: PrimaryStageListener, val upperSceneListener: BackClickListener) extends DefaultBackScene(parentStage, upperSceneListener) with MainMenuBarListener {

  /**
    * The upper main menu bar
    */
  protected val menuBar = new MainMenuBar(this)

  private val fullScreen = new CheckBox("Fullscreen") {
    //onAction = _ => upperSceneListener.onMultiPlayerSceneBackClick()
  }

  /*private val goBack = new Button("Back to menù") {
    onAction = _ => upperSceneListener.onMultiPlayerSceneBackClick()
  }*/

  /**
    * The central level container
    */
  protected val container: VBox = new VBox {
    alignment = Pos.Center
    children = Seq(fullScreen, goBack)
  }

  /* Setting the root container*/
  root = container

  override def onFullScreenSettingClick(): Unit = {
    parentStage.fullScreen = !parentStage.fullScreen.get()
  }

}

/**
  * Trait which gets notified when a SettingsScene event occurs
  */
trait SettingsSceneListener {

  /**
    * This method called when the level context has been created
    *
    * @param levelContext the new level context
    * @param level        the new level index
    */
  def onLevelContextCreated(levelContext: LevelContext, level: Int)
}
