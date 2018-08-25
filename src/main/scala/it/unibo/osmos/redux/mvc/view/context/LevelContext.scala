package it.unibo.osmos.redux.mvc.view.context

import java.util.UUID

import it.unibo.osmos.redux.mvc.model.MapShape
import it.unibo.osmos.redux.mvc.view.drawables.{DrawableWrapper, EntitiesDrawer}
import it.unibo.osmos.redux.mvc.view.events._

/**
  * Trait modelling the context of a level
  */
trait LevelContext extends EventWrapperObservable[MouseEventWrapper] with EntitiesDrawer with GameStateHolder {

  /**
    * The level context type
    */
  val levelContextType: LevelContextType.Value

  /**
    * Called once at the beginning at the level. Manages the context setup
    * @param mapShape the level shape
    */
  def setupLevel(mapShape: MapShape)

  /**
    * Setter. Sets the level context listener
    * @param levelContextListener the level context listener
    */
  def setListener(levelContextListener: LevelContextListener)

  /**
    * Called when the LevelContext gets a mouse event from the scene
    * @param event the mouse event
    */
  def notifyMouseEvent(event: MouseEventWrapper)
}

/**
  * LevelContext used in multplayer sessions
  */
trait MultiPlayerLevelContext extends LevelContext {

  /**
    * The level context UUID, used in multiplayer to discriminate between different users
    * @return the uuid
    */
  def getUUID: UUID

}

/**
  * Companion object
  */
object LevelContext {

  def apply(): LevelContext = new LevelContextImpl()

  def apply(levelContextType: LevelContextType.Value): LevelContext = levelContextType match {
    case LevelContextType.multiplayer => new MultiPlayerLevelContextImpl
    case _ => new LevelContextImpl(levelContextType)
  }

  /**
    * Base abstract implementation of the LevelContext trait
    */
  private abstract class AbstractLevelContext(override val levelContextType: LevelContextType.Value = LevelContextType.normal) extends LevelContext {

    /**
      * The level context listener
      */
    protected var listener: Option[LevelContextListener] = Option.empty
    override def setListener(levelContextListener: LevelContextListener): Unit = listener = Option(levelContextListener)

    /**
      * A reference to the mouse event listener
      */
    private var mouseEventObserver: Option[EventWrapperObserver[MouseEventWrapper]] = Option.empty

    override def setupLevel(mapShape: MapShape): Unit = listener match {
      case Some(l) => l.onLevelSetup(mapShape)
      case _ =>
    }

    override def notifyMouseEvent(event: MouseEventWrapper): Unit = mouseEventObserver match {
      case Some(meo) => meo.notify(event)
      case _ =>
    }

    override def drawEntities(playerEntity: Option[DrawableWrapper], entities: Seq[DrawableWrapper]): Unit = listener match {
      case Some(l) => l.onDrawEntities(playerEntity, entities)
      case _ =>
    }

    override def subscribe(eventObserver: EventWrapperObserver[MouseEventWrapper]): Unit = mouseEventObserver = Option(eventObserver)

    override def unsubscribe(eventObserver: EventWrapperObserver[MouseEventWrapper]): Unit = mouseEventObserver = Option.empty

  }

  /**
    * Implementation of the LevelContext trait
    */
  private class LevelContextImpl(override val levelContextType: LevelContextType.Value = LevelContextType.normal) extends AbstractLevelContext(levelContextType) {

    /**
      * The current game state
      */
    private[this] var _gameCurrentState: GameStateEventWrapper = GamePending

    def gameCurrentState: GameStateEventWrapper = _gameCurrentState

    def gameCurrentState_=(value: GameStateEventWrapper): Unit = {
      _gameCurrentState = value
      listener match {
        case Some(l) =>
          gameCurrentState match {
            case GameWon => l.onLevelEnd(true)
            case GameLost => l.onLevelEnd(false)
            case _ =>
        }
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
    }
  }

  /**
    * Implementation of the MultiPlayerLevelContext trait, override LevelContextImpl
    */
  private class MultiPlayerLevelContextImpl() extends LevelContextImpl() with MultiPlayerLevelContext {

    override def getUUID: UUID = UUID.randomUUID()
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
