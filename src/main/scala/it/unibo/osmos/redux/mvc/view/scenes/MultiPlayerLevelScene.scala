package it.unibo.osmos.redux.mvc.view.scenes
import it.unibo.osmos.redux.mvc.controller.levels.structure.LevelInfo
import it.unibo.osmos.redux.mvc.view.components.level.LevelScreen
import it.unibo.osmos.redux.mvc.view.context.{LevelContext, MultiPlayerLevelContext, MultiPlayerLevelContextListener}
import scalafx.animation.FadeTransition
import scalafx.scene.paint.Color
import scalafx.stage.Stage
import scalafx.util.Duration

/** This scene holds and manages a single level played in multiplayer mode
  *
  * @param parentStage        the parent stage
  * @param levelInfo          the level info
  * @param listener           the listener
  * @param upperSceneListener the upper scene listener to manage the previously scene events
  */
class MultiPlayerLevelScene(override val parentStage: Stage, override val levelInfo: LevelInfo, override val listener: LevelSceneListener, override val upperSceneListener: BackClickListener)
  extends LevelScene(parentStage, levelInfo, listener, upperSceneListener) with MultiPlayerLevelContextListener {

  /** The level context, created with the MultiPlayerLevelScene. It must be a MultiPlayerLevelContext */
  override def levelContext: Option[ _ <: LevelContext] = _levelContext
  override def levelContext_= (levelContext: LevelContext): Unit = levelContext match {
    case mplc: MultiPlayerLevelContext => _levelContext = Option(mplc)
    case _ => throw new IllegalArgumentException("MultiPLayerLevelScene must use a MultiPlayerLevelContext")
  }

  /** Called when we lost as a server */
  override def onLevelLostAsServer(): Unit = {
    LevelState.inputEnabled = false

    /** Creating an end screen with a button */
    val endScreenAsServer = LevelScreen.Builder(this)
      .withText("You lost, but other players are still playing. Please wait...", 50, Color.White)
      .build()
    endScreenAsServer.opacity = 0.0

    /** Fade in/fade out transition */
    new FadeTransition(Duration.apply(3000), canvas) {
      fromValue = 1.0
      toValue = 0.0
      onFinished = _ => {
        /** Remove all the contents and add the end screen */
        content.clear()
        content.add(endScreenAsServer)
        new FadeTransition(Duration.apply(3000), endScreenAsServer) {
          fromValue = 0.0
          toValue = 1.0
        }.play()
      }
    }.play()
  }
}
