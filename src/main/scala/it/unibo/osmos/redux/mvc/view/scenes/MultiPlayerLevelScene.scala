package it.unibo.osmos.redux.mvc.view.scenes
import it.unibo.osmos.redux.mvc.view.components.level.LevelStateBox
import it.unibo.osmos.redux.mvc.view.context.{LevelContext, MultiPlayerLevelContext}
import it.unibo.osmos.redux.mvc.view.events.MouseEventWrapper
import it.unibo.osmos.redux.utils.Point
import javafx.scene.input.MouseEvent
import scalafx.stage.Stage

class MultiPlayerLevelScene(override val parentStage: Stage, override val listener: LevelSceneListener, override val upperSceneListener: UpperLevelSceneListener)
  extends LevelScene(parentStage, listener, upperSceneListener) {

  /**
    * The level context, created with the MultiPlayerLevelScene. It must be a MultiPlayerLevelContext
    */
  override def levelContext: Option[ _ <: LevelContext] = _levelContext
  override def levelContext_= (levelContext: LevelContext): Unit = levelContext match {
    case mplc: MultiPlayerLevelContext => _levelContext = Option(mplc)
    case _ => throw new IllegalArgumentException("MultiPLayerLevelScene must use a MultiPlayerLevelContext")
  }

  /**
    * In multiplayer mode we must also send the level context UUID to let the serve discriminate between users
    * @param mouseEvent the mouse event
    */
  override protected def sendMouseEvent(mouseEvent: MouseEvent): Unit = levelContext match {
    case Some(mplc) => mplc notifyMouseEvent MouseEventWrapper(Point(mouseEvent.getX, mouseEvent.getY), mplc.getPlayerUUID)
    case _ =>
  }

  /**
    * Pause button should be disabled in multiplayer mode
    */
  override protected val levelStateBox: LevelStateBox = new LevelStateBox(this, 4.0, showPauseButton = false)
  override def onPause(): Unit = throw new UnsupportedOperationException("Users cannot pause the game in multiplayer mode")
  override def onResume(): Unit = throw new UnsupportedOperationException("Users cannot resume the game in multiplayer mode")

}
