package it.unibo.osmos.redux.mvc.view.context

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

  /**
    * The UUID of the current player, used to discriminate between different users
    * @return Some(uuid) if it's defined and the current game mode is not simulation; otherwise None.
    */
  override def getPlayerUUID: String

  /**
    * Sets the current player uuid.
    */
  def setPlayerUUID(playerUUID: String): Unit
}

/**
  * LevelContext used in multplayer sessions
  */
trait MultiPlayerLevelContext extends LevelContext {

}

/**
  * Companion object
  */
object LevelContext {

  /**
    * Apply method to create a new single-player level context
    * @param isSimulation If it's a simulation or not.
    * @return A new instance of a LevelContext
    */
  def apply(isSimulation: Boolean): LevelContext =
    new LevelContextImpl(levelContextType = if (isSimulation) LevelContextType.simulation else LevelContextType.normal)

  /**
    * Apply method to create a new multi-player level context
    * @param playerUUID The current player UUID
    * @return A new instance of a MultiPlayerLevelContext
    */
  def apply(playerUUID: String): MultiPlayerLevelContext = new MultiPlayerLevelContextImpl(playerUUID)

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
  private class LevelContextImpl(private var playerUUID: String = "", override val levelContextType: LevelContextType.Value = LevelContextType.normal) extends AbstractLevelContext(levelContextType) {

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

    override def getPlayerUUID: String = playerUUID

    //TODO: player uuid is always available once the controller parses the level definition from the disk. So this setter is mandatory. Another way would be to let the interface to load level definition to have the player uuid available before the creation of the level context.
    override def setPlayerUUID(playerUUID: String): Unit = this.playerUUID = playerUUID

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
  private class MultiPlayerLevelContextImpl(private val playerUUID: String) extends LevelContextImpl(playerUUID, LevelContextType.multiplayer) with MultiPlayerLevelContext {
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
