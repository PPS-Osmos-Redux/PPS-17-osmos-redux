package it.unibo.osmos.redux.mvc.view.scenes
import it.unibo.osmos.redux.mvc.controller.levels.structure.LevelInfo
import it.unibo.osmos.redux.mvc.view.context.{LevelContext, MultiPlayerLevelContext}
import scalafx.stage.Stage

/**
  * This scene holds and manages a single level played in multiplayer mode
  *
  * @param parentStage        the parent stage
  * @param levelInfo          the level info
  * @param listener           the listener
  * @param upperSceneListener the upper scene listener to manage the previously scene events
  */
class MultiPlayerLevelScene(override val parentStage: Stage, override val levelInfo: LevelInfo, override val listener: LevelSceneListener, override val upperSceneListener: BackClickListener)
  extends LevelScene(parentStage, levelInfo, listener, upperSceneListener) {

  /** The level context, created with the MultiPlayerLevelScene. It must be a MultiPlayerLevelContext */
  override def levelContext: Option[ _ <: LevelContext] = _levelContext
  override def levelContext_= (levelContext: LevelContext): Unit = levelContext match {
    case mplc: MultiPlayerLevelContext => _levelContext = Option(mplc)
    case _ => throw new IllegalArgumentException("MultiPLayerLevelScene must use a MultiPlayerLevelContext")
  }

}
