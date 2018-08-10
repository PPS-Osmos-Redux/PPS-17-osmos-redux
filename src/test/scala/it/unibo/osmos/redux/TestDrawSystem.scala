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

  val acceleration = AccelerationComponent(1, 1)
  val collidable = CollidableComponent(true)
  val speed = SpeedComponent(4, 0)
  val dimension = DimensionComponent(5)
  val position = PositionComponent(Point(0, 0))
  val visible = VisibleComponent(true)
  val notVisible = VisibleComponent(false)
  val typeEntity = TypeComponent(EntityType.Material)

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

  test("PlayerCellEntity is present, but not visible"){
    val spy = DrawSystemSpy()
    val system = DrawSystem(spy, 1)
    val pce = PlayerCellEntity(acceleration,collidable,dimension,position,speed,notVisible,typeEntity)
    EntityManager.add(pce)
    system.update()
    assert(spy.player.isEmpty)
  }

  test("PlayerCellEntity is present and visible"){
    val spy = DrawSystemSpy()
    val system = DrawSystem(spy, 1)
    val pce = PlayerCellEntity(acceleration,collidable,dimension,position,speed,visible,typeEntity)
    EntityManager.add(pce)
    system.update()
    assert(spy.player.isDefined)
  }

  test("PlayerCellEntity correctly wrapped"){
    val spy = DrawSystemSpy()
    val system = DrawSystem(spy, 1)
    val pce = PlayerCellEntity(acceleration,collidable,dimension,position,speed,visible,typeEntity)
    EntityManager.add(pce)
    system.update()
    val playerWrapped = spy.player.get
    assert(playerWrapped.center.equals(pce.getPositionComponent.point))
    assert(playerWrapped.radius.equals(pce.getDimensionComponent.radius))
    playerWrapped.entityType.equals(pce.getTypeComponent.typeEntity)
  }
}
