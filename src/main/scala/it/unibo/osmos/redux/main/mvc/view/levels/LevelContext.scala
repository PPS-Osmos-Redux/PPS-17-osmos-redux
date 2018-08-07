package it.unibo.osmos.redux.main.mvc.view.levels

import javafx.event.EventHandler
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
    */
  def drawEntities()

  /**
    * This method register a single mouse event listener
    * @param eventHandler the listener
    */
  def registerMouseEventListener(eventHandler: EventHandler[MouseEvent])

  /**
    * This method unregister a single mouse event listener
    * @param eventHandler the listener
    */
  def unregisterMouseEventListener(eventHandler: EventHandler[MouseEvent])

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
    private var mouseEventListener: Option[EventHandler[MouseEvent]] = Option.empty

    override def setupLevel(): Unit = {
      //TODO: waiting for controller
    }

    override def drawEntities(): Unit = {
      //TODO: waiting for controller
      val drawables = Seq()
      listener.onDrawEntities(drawables)
    }

    override def registerMouseEventListener(eventHandler: EventHandler[MouseEvent]): Unit = mouseEventListener = Option(eventHandler)

    override def unregisterMouseEventListener(eventHandler: EventHandler[MouseEvent]): Unit = mouseEventListener = Option.empty

    override def pushMouseEvent(mouseEvent: MouseEvent): Unit = {
      if (mouseEventListener.nonEmpty) {
        mouseEventListener.get handle mouseEvent
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
    * @param drawables a collection of coordinates
    */
  //TODO: modify this according to the controller
  def onDrawEntities(drawables: Seq[(Double, Double, Double, Double)])
}