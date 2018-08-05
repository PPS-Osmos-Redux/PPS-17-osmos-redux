package it.unibo.osmos.redux.main.mvc.view.stages

import it.unibo.osmos.redux.main.mvc.view.ViewConstants
import it.unibo.osmos.redux.main.mvc.view.scenes.MainScene
import scalafx.application.JFXApp

/**
  * Primary stage which holds and manages all the different game scenes
  */
trait OsmosReduxPrimaryStage {

}

/**
  * Companion object
  */
object OsmosReduxPrimaryStage {
  def apply(fullScreenEnabled: Boolean = false,
            windowWidth: Double = ViewConstants.defaultWindowWidth,
            windowHeight: Double = ViewConstants.defaultWindowHeight): OsmosReduxPrimaryStageImpl = new OsmosReduxPrimaryStageImpl(fullScreenEnabled, windowWidth, windowHeight)

  /**
    * Primary stage implementation
    * @param fullScreenEnabled true if we want the stage to be shown fullscreen, false otherwise
    * @param windowWidth the window width
    * @param windowHeight the window height
    */
  class OsmosReduxPrimaryStageImpl(val fullScreenEnabled: Boolean, val windowWidth: Double, val windowHeight: Double) extends JFXApp.PrimaryStage with OsmosReduxPrimaryStage {
    title = ViewConstants.defaultWindowTitle
    fullScreen = fullScreenEnabled
    width = windowWidth
    height = windowHeight

    scene = new MainScene(this)
  }
}
