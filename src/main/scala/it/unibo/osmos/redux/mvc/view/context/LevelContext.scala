package it.unibo.osmos.redux.mvc.view.context

import it.unibo.osmos.redux.mvc.model.MapShape
import it.unibo.osmos.redux.mvc.view.drawables.{DrawableWrapper, EntitiesDrawer}
import it.unibo.osmos.redux.mvc.view.events._

/**
  * Trait modelling the context of a level
  */
trait LevelContext extends EventWrapperObservable[MouseEventWrapper] with EntitiesDrawer with GameStateHolder {
  /**
    * Called once at the beginning at the level. Manages the context setup
    * @param mapShape the level shape
    */
  def setupLevel(mapShape: MapShape)

  /**
    * Called when the LevelContext gets a mouse event from the scene
    * @param event the mouse event
    */
  def notifyMouseEvent(event: MouseEventWrapper)
}

/**
  * Companion object
  */
object LevelContext {

  def apply(listener: LevelContextListener): LevelContext = new LevelContextImpl(listener)

  def apply(listener: LevelContextListener, levelContextType: LevelContextType.Value): LevelContext = new LevelContextImpl(listener, levelContextType)

  /**
    * Base abstract implementation of the LevelContext trait
    * @param listener the LevelContextListener instance
    */
  private abstract class AbstractLevelContext(private val listener: LevelContextListener, val levelContextType: LevelContextType.Value = LevelContextType.normal) extends LevelContext {

    /**
      * A reference to the mouse event listener
      */
    private var mouseEventObserver: Option[EventWrapperObserver[MouseEventWrapper]] = Option.empty

    override def setupLevel(mapShape: MapShape): Unit = listener.onLevelSetup(mapShape)

    override def notifyMouseEvent(event: MouseEventWrapper): Unit = mouseEventObserver match {
      case Some(e) => e.notify(event)
      case _ =>
    }

    override def drawEntities(playerEntity: Option[DrawableWrapper], entities: Seq[DrawableWrapper]): Unit = listener.onDrawEntities(playerEntity, entities)

    override def subscribe(eventObserver: EventWrapperObserver[MouseEventWrapper]): Unit = mouseEventObserver = Option(eventObserver)

    override def unsubscribe(eventObserver: EventWrapperObserver[MouseEventWrapper]): Unit = mouseEventObserver = Option.empty

  }

  /**
    * Implementation of the LevelContext trait
    * @param listener the LevelContextListener instance
    */
  private class LevelContextImpl(private val listener: LevelContextListener, override val levelContextType: LevelContextType.Value = LevelContextType.normal) extends AbstractLevelContext(listener, levelContextType) {

    /**
      * The current game state
      */
    private[this] var _gameCurrentState: GameStateEventWrapper = GamePending

    def gameCurrentState: GameStateEventWrapper = _gameCurrentState

    def gameCurrentState_=(value: GameStateEventWrapper): Unit = {
      _gameCurrentState = value
      gameCurrentState match {
        case GameWon => listener.onLevelEnd(true)
        case GameLost => listener.onLevelEnd(false)
        case _ =>
      }
    }

    /**
      * Called on a event T type
      *
      * @param event the event
      */
    override def notify(event: GameStateEventWrapper): Unit = {
      gameCurrentState_=(event)
      gameCurrentState match {
        case GameWon => listener.onLevelEnd(true)
        case GameLost => listener.onLevelEnd(false)
        case _ =>
      }
    }
  }

}

/**
  * Trait which gets notified when a LevelContext event occurs
  */
trait LevelContextListener {
  /**
    * Called when the LevelContext retrieve a collection of entities that must be drawn on the scene
    * @param playerEntity the player entity. It may be empty
    * @param entities the other entities
    */
  def onDrawEntities(playerEntity: Option[DrawableWrapper], entities: Seq[DrawableWrapper])

  /**
    * Called once. Manages the context setup communicating the level shape
    * @param mapShape the level shape
    */
  def onLevelSetup(mapShape: MapShape)

  /**
    * Called once when the level ends.
    * @param levelResult true if the player has won, false otherwise
    */
  def onLevelEnd(levelResult: Boolean)
}
