package it.unibo.osmos.redux

import it.unibo.osmos.redux.ecs.entities._
import it.unibo.osmos.redux.ecs.entities.builders.CellBuilder
import it.unibo.osmos.redux.ecs.entities.properties.composed.DrawableProperty
import it.unibo.osmos.redux.ecs.systems.DrawSystem
import it.unibo.osmos.redux.mvc.model.MapShape
import it.unibo.osmos.redux.mvc.view.context.{LevelContext, LevelContextListener, LevelContextType}
import it.unibo.osmos.redux.mvc.view.drawables.DrawableWrapper
import it.unibo.osmos.redux.mvc.view.events.{EventWrapperObserver, GameStateEventWrapper, MouseEventWrapper}
import org.scalatest.{BeforeAndAfter, FunSuite}

/**
  * Spy class to capture the indirect output of DrawSystem
  */
case class DrawSystemSpy() extends LevelContext {

  override val levelContextType: LevelContextType.Value = LevelContextType.normal
  protected var _listener: Option[LevelContextListener] = Option.empty
  private var _player: Option[DrawableWrapper] = None
  private var _playerUUID: String = _
  private var _entities: Seq[DrawableWrapper] = Seq()

  def player: Option[DrawableWrapper] = _player

  def entities: Seq[DrawableWrapper] = _entities

  override def setupLevel(mapShape: MapShape): Unit = ???

  override def drawEntities(playerEntity: Option[DrawableWrapper], entities: Seq[DrawableWrapper]): Unit = {
    _player = playerEntity
    _entities = entities
  }

  override def gameCurrentState: GameStateEventWrapper = ???

  override def subscribe(eventObserver: EventWrapperObserver[MouseEventWrapper]): Unit = ???

  override def unsubscribe(eventObserver: EventWrapperObserver[MouseEventWrapper]): Unit = ???

  override def notify(event: GameStateEventWrapper): Unit = ???

  override def notifyMouseEvent(event: MouseEventWrapper): Unit = ???

  override def setListener(levelContextListener: LevelContextListener): Unit = _listener = Option(levelContextListener)

  override def getPlayerUUID: String = _playerUUID

  override def setPlayerUUID(playerUUID: String): Unit = _playerUUID = playerUUID
}

/**
  * Test for DrawSystem
  */
class TestDrawSystem extends FunSuite with BeforeAndAfter {

  var spy: DrawSystemSpy = _
  var system: DrawSystem = _

  before{
    spy = DrawSystemSpy()
    system = DrawSystem(spy)
  }

  after(EntityManager.clear())

  test("PlayerCellEntity not present") {
    system.update()
    assert(spy.player.isEmpty)
  }

  test("CellEntity enemies not present") {
    system.update()
    assert(spy.entities.isEmpty)
  }

  test("PlayerCellEntity is present, but not visible") {
    val pce = CellBuilder().visible(false).buildPlayerEntity()
    EntityManager.add(pce)
    spy.setPlayerUUID(pce.getUUID)
    system.update()
    assert(spy.player.isEmpty)
  }

  test("PlayerCellEntity is present and visible") {
    val pce = CellBuilder().buildPlayerEntity()
    EntityManager.add(pce)
    spy.setPlayerUUID(pce.getUUID)
    system.update()
    assert(spy.player.isDefined)
  }

  test("PlayerCellEntity correctly wrapped") {
    val pce = CellBuilder().buildPlayerEntity()
    EntityManager.add(pce)
    spy.setPlayerUUID(pce.getUUID)
    system.update()
    val playerWrapped = spy.player.get
    assert(playerWrapped.center.equals(pce.getPositionComponent.point))
    assert(playerWrapped.radius.equals(pce.getDimensionComponent.radius))
    assert(playerWrapped.entityType.equals(pce.getTypeComponent.typeEntity))
    assert(playerWrapped.speed._1 === pce.getSpeedComponent.vector.x)
    assert(playerWrapped.speed._2 === pce.getSpeedComponent.vector.y)
  }

  test("filter visible CellEntity") {
    val visibleCE = CellBuilder().withDimension(5).withPosition(3,3).buildCellEntity()
    val notVisibleCE = CellBuilder().withDimension(7).withPosition(4,4).visible(false).buildCellEntity()
    EntityManager.add(visibleCE)
    EntityManager.add(notVisibleCE)
    system.update()
    assert(spy.entities.size == 1)
  }

  test("CellEntity enemies correctly wrapped") {
    val visibleCE = CellBuilder().withDimension(5).withPosition(3,3).withSpeed(2,-2).buildCellEntity()
    val visibleCE1 = CellBuilder().withDimension(7).withPosition(4,4).withSpeed(3,4).buildCellEntity()
    EntityManager.add(visibleCE)
    EntityManager.add(visibleCE1)
    system.update()
    checkEnemies(spy.entities, visibleCE)
    checkEnemies(spy.entities, visibleCE1)
  }

  private def checkEnemies(enemiesWrapped: Seq[DrawableWrapper], enemy: DrawableProperty): Unit = {
    assert(enemiesWrapped.exists(p => p.center.equals(enemy.getPositionComponent.point) &&
      p.radius.equals(enemy.getDimensionComponent.radius) &&
      p.entityType.equals(enemy.getTypeComponent.typeEntity) &&
      p.speed._1 === enemy.getSpeedComponent.vector.x &&
      p.speed._2 === enemy.getSpeedComponent.vector.y))
  }
}
