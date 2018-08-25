package it.unibo.osmos.redux.mvc.view.scenes
import it.unibo.osmos.redux.mvc.view.components.level.LevelStateBox
import scalafx.stage.Stage

class MultiPlayerLevelScene(override val parentStage: Stage, override val listener: LevelSceneListener, override val upperSceneListener: UpperLevelSceneListener)
  extends LevelScene(parentStage, listener, upperSceneListener) {

  /**
    * Pause button should be disabled in multiplayer mode
    */
  override protected val levelStateBox: LevelStateBox = new LevelStateBox(this, 4.0, showPauseButton = false)
  override def onPause(): Unit = throw new UnsupportedOperationException("Users cannot pause the game in multiplayer mode")
  override def onResume(): Unit = throw new UnsupportedOperationException("Users cannot resume the game in multiplayer mode")


}
