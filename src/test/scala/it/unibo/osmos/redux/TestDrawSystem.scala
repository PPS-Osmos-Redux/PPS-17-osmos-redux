package it.unibo.osmos.redux

import it.unibo.osmos.redux.main.ecs.components._
import it.unibo.osmos.redux.main.ecs.entities.{CellEntity, EntityManager, PlayerCellEntity}
import it.unibo.osmos.redux.main.mvc.view.drawables.DrawableWrapper
import it.unibo.osmos.redux.main.mvc.view.events.MouseEventListener
import it.unibo.osmos.redux.main.mvc.view.levels.LevelContext
import it.unibo.osmos.redux.main.ecs.systems.DrawSystem
import it.unibo.osmos.redux.main.utils.Point
import javafx.scene.input.MouseEvent
import org.scalatest.FunSuite

/**
  * Spy class to capture the indirect output of DrawSystem
  */
case class DrawSystemSpy() extends LevelContext {

  private var _player: Option[DrawableWrapper] = None
  private var _entities: Seq[DrawableWrapper] = Seq()

  def player: Option[DrawableWrapper] = _player
  def entities: Seq[DrawableWrapper] = _entities

  override def setupLevel(): Unit = ???

  override def drawEntities(playerEntity: Option[DrawableWrapper], entities: Seq[DrawableWrapper]): Unit = {
    _player = playerEntity
    _entities = entities
  }

  override def registerMouseEventListener(mouseEventListener: MouseEventListener): Unit = ???

  override def unregisterMouseEventListener(mouseEventListener: MouseEventListener): Unit = ???

  override def pushMouseEvent(mouseEvent: MouseEvent): Unit = ???
}

class TestDrawSystem extends FunSuite {

  test("PlayerCellEntity not present"){
    val spy = DrawSystemSpy()
    val system = DrawSystem(spy, 1)
    system.update()
    assert(spy.player.isEmpty)
  }

  test("CellEntity enemies not present"){
    val spy = DrawSystemSpy()
    val system = DrawSystem(spy, 1)
    system.update()
    assert(spy.entities.isEmpty)
  }
}
