package it.unibo.osmos.redux.main.mvc.view.levels

import it.unibo.osmos.redux.main.mvc.view.drawables.DrawableWrapper
import it.unibo.osmos.redux.main.mvc.view.events.{MouseEventListener, MouseEventWrapper}
import it.unibo.osmos.redux.main.utils.Point
import javafx.scene.input.MouseEvent

/**
  * Trait modelling the context of a level
  */
trait LevelContext {
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

  /**
    * This method register a single mouse event listener
    * @param mouseEventListener the listener
    */
  def registerMouseEventListener(mouseEventListener: MouseEventListener)

  /**
    * This method unregister a single mouse event listener
    * @param mouseEventListener the listener
    */
  def unregisterMouseEventListener(mouseEventListener: MouseEventListener)

  /**
    * This method pushes a mouse event to the registered listener
    * @param mouseEvent the mouse event
    */
  def pushMouseEvent(mouseEvent: MouseEvent)
}

object LevelContext {

  def apply(listener: LevelContextListener): LevelContext = new LevelContextImpl(listener)

  /**
    * Implementation of the LevelContext trait
    * @param listener the LevelContextListener instance
    */
  private class LevelContextImpl(private val listener: LevelContextListener) extends LevelContext {

    /**
      * A reference to the mouse event listener
      */
    private var mouseEventListener: Option[MouseEventListener] = Option.empty

    override def setupLevel(): Unit = {
      //TODO: waiting for controller
    }

    override def drawEntities(playerEntity: Option[DrawableWrapper], entities: Seq[DrawableWrapper]): Unit = listener.onDrawEntities(playerEntity, entities)

    override def registerMouseEventListener(eventHandler: MouseEventListener): Unit = mouseEventListener = Option(eventHandler)

    override def unregisterMouseEventListener(eventHandler: MouseEventListener): Unit = mouseEventListener = Option.empty

    override def pushMouseEvent(mouseEvent: MouseEvent): Unit = {
      mouseEventListener match {
        case Some(e) => e.onEvent(MouseEventWrapper(Point(mouseEvent.getX, mouseEvent.getSceneY)))
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
  //TODO: modify this according to the controller
  def onDrawEntities(playerEntity: Option[DrawableWrapper], entities: Seq[DrawableWrapper])
}
