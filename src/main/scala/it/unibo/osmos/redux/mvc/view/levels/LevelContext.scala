package it.unibo.osmos.redux.mvc.view.levels

import it.unibo.osmos.redux.mvc.view.drawables.DrawableWrapper
import it.unibo.osmos.redux.mvc.view.events.{EventWrapperListener, EventWrapperSource, MouseEventWrapper}

/**
  * Trait modelling the context of a level
  */
trait LevelContext extends EventWrapperSource[MouseEventWrapper]{
  /**
    * Called once at the beginning at the level. Manages the context creation
    */
  def setupLevel()

  /**
    * Called once per frame. Manages the entities that must be drawn
    * @param playerEntity the player entity. It may be empty
    * @param entities the other entities
    */
  def drawEntities(playerEntity: Option[DrawableWrapper], entities: Seq[DrawableWrapper])
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

    override def setupLevel(): Unit = {
      //TODO: waiting for controller
      println("Level started")
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
}
