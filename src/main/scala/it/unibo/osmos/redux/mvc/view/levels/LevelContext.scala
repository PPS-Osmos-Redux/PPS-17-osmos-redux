package it.unibo.osmos.redux.mvc.view.levels

import it.unibo.osmos.redux.mvc.model.MapShape
import it.unibo.osmos.redux.mvc.view.drawables.{DrawableWrapper, EntitiesDrawer}
import it.unibo.osmos.redux.mvc.view.events._

/**
  * Trait modelling the context of a level
  */
trait LevelContext extends EventWrapperSource[MouseEventWrapper] with EntitiesDrawer with GameStateHolder {
  /**
    * Called once at the beginning at the level. Manages the context setup
    * @param mapShape the level shape
    */
  def setupLevel(mapShape: MapShape)
}

/**
  * Trait modelling an object which holds the current game state and reacts when it gets changed
  */
trait GameStateHolder extends EventWrapperListener[GameStateEventWrapper] {

  /**
    * A generic definition of the game state
    * @return a GameStateEventWrapper
    */
  def gameCurrentState: GameStateEventWrapper
}

object LevelContext {

  def apply(listener: LevelContextListener, simulation: Boolean): LevelContext = new LevelContextImpl(listener, simulation)

  /**
    * Implementation of the LevelContext trait
    * @param listener the LevelContextListener instance
    */
  private class LevelContextImpl(private val listener: LevelContextListener, val simulation: Boolean) extends LevelContext {

    /**
      * A reference to the mouse event listener
      */
    private var mouseEventListener: Option[EventWrapperListener[MouseEventWrapper]] = Option.empty

    override def setupLevel(mapShape: MapShape): Unit = {
    }

    override def drawEntities(playerEntity: Option[DrawableWrapper], entities: Seq[DrawableWrapper]): Unit = listener.onDrawEntities(playerEntity, entities)

    override def registerEventListener(eventListener: EventWrapperListener[MouseEventWrapper]): Unit = mouseEventListener = Option(eventListener)

    override def unregisterEventListener(eventListener: EventWrapperListener[MouseEventWrapper]): Unit = mouseEventListener = Option.empty

    override def pushEvent(event: MouseEventWrapper): Unit = {
      mouseEventListener match {
        case Some(e) => e.onEvent(event)
        case _ =>
      }
    }

    /**
      * The current game state
      */
    private[this] var _gameCurrentState: GameStateEventWrapper = GamePending

    def gameCurrentState: GameStateEventWrapper = _gameCurrentState

    def gameCurrentState_=(value: GameStateEventWrapper): Unit = {
      _gameCurrentState = value
    }

    /**
      * Called on a event T type
      *
      * @param event the event
      */
    //TODO: react properly to events (showing screen)
    override def onEvent(event: GameStateEventWrapper): Unit = gameCurrentState_=(event)
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
}
