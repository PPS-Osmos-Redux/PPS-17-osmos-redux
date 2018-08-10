package it.unibo.osmos.redux

import it.unibo.osmos.redux.main.mvc.view.drawables.DrawableWrapper
import it.unibo.osmos.redux.main.mvc.view.events.MouseEventListener
import it.unibo.osmos.redux.main.mvc.view.levels.LevelContext
import javafx.scene.input.MouseEvent

/**
  * Spy class to capture the indirect output of DrawSystem
  */
case class DrawSystemSpy() extends LevelContext {

  private var _player: Option[DrawableWrapper] = None
  private var _entities: Seq[DrawableWrapper] = Seq()

  def player = _player
  def entities = _entities

  override def setupLevel(): Unit = ???

  override def drawEntities(playerEntity: Option[DrawableWrapper], entities: Seq[DrawableWrapper]): Unit = {
    _player = playerEntity
    _entities = entities
  }

  override def registerMouseEventListener(mouseEventListener: MouseEventListener): Unit = ???

  override def unregisterMouseEventListener(mouseEventListener: MouseEventListener): Unit = ???

  override def pushMouseEvent(mouseEvent: MouseEvent): Unit = ???
}

class TestDrawSystem {

}
