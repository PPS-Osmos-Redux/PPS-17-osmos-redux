package it.unibo.osmos.redux

import it.unibo.osmos.redux.ecs.components._
import it.unibo.osmos.redux.ecs.entities.{CellEntity, DrawableProperty, EntityManager, PlayerCellEntity}
import it.unibo.osmos.redux.ecs.systems.DrawSystem
import it.unibo.osmos.redux.mvc.model.MapShape
import it.unibo.osmos.redux.mvc.view.drawables.DrawableWrapper
import it.unibo.osmos.redux.mvc.view.events.{EventWrapperObserver, GameStateEventWrapper, MouseEventWrapper}
import it.unibo.osmos.redux.mvc.view.context.{LevelContext, LevelContextListener, LevelContextType}
import it.unibo.osmos.redux.utils.Point
import org.scalatest.{BeforeAndAfter, FunSuite}

/**
  * Spy class to capture the indirect output of DrawSystem
  */
case class DrawSystemSpy() extends LevelContext {

  private var _player: Option[DrawableWrapper] = None
  private var _entities: Seq[DrawableWrapper] = Seq()
  protected var _listener: Option[LevelContextListener] = Option.empty

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

  override val levelContextType: LevelContextType.Value = LevelContextType.normal

  override def setListener(levelContextListener: LevelContextListener): Unit = _listener = Option(levelContextListener)
}

/**
  * Test for DrawSystem
  */
class TestDrawSystem extends FunSuite with BeforeAndAfter {

  val acceleration = AccelerationComponent(1, 1)
  val collidable = CollidableComponent(true)
  val speed = SpeedComponent(4, 0)
  val speed1 = SpeedComponent(3, 2)
  val dimension = DimensionComponent(5)
  val position = PositionComponent(Point(0, 0))
  val visible = VisibleComponent(true)
  val notVisible = VisibleComponent(false)
  val typeEntity = TypeComponent(EntityType.Matter)
  val dimension1 = DimensionComponent(3)
  val position1 = PositionComponent(Point(3, 4))
  val spawner = SpawnerComponent(false)

  after(EntityManager.clear())

  test("PlayerCellEntity not present") {
    val spy = DrawSystemSpy()
    val system = DrawSystem(spy)
    system.update()
    assert(spy.player.isEmpty)
  }

  test("CellEntity enemies not present") {
    val spy = DrawSystemSpy()
    val system = DrawSystem(spy)
    system.update()
    assert(spy.entities.isEmpty)
  }

  test("PlayerCellEntity is present, but not visible") {
    val spy = DrawSystemSpy()
    val system = DrawSystem(spy)
    val pce = PlayerCellEntity(acceleration, collidable, dimension, position, speed, notVisible, typeEntity, spawner)
    EntityManager.add(pce)
    system.update()
    assert(spy.player.isEmpty)
  }

  test("PlayerCellEntity is present and visible") {
    val spy = DrawSystemSpy()
    val system = DrawSystem(spy)
    val pce = PlayerCellEntity(acceleration, collidable, dimension, position, speed, visible, typeEntity, spawner)
    EntityManager.add(pce)
    system.update()
    assert(spy.player.isDefined)
  }

  test("PlayerCellEntity correctly wrapped") {
    val spy = DrawSystemSpy()
    val system = DrawSystem(spy)
    val pce = PlayerCellEntity(acceleration, collidable, dimension, position, speed, visible, typeEntity, spawner)
    EntityManager.add(pce)
    system.update()
    val playerWrapped = spy.player.get
    assert(playerWrapped.center.equals(pce.getPositionComponent.point))
    assert(playerWrapped.radius.equals(pce.getDimensionComponent.radius))
    assert(playerWrapped.entityType.equals(pce.getTypeComponent.typeEntity))
    assert(playerWrapped.speed._1 === speed.vector.x)
    assert(playerWrapped.speed._2 === speed.vector.y)
  }

  test("filter visible CellEntity") {
    val spy = DrawSystemSpy()
    val system = DrawSystem(spy)
    val visibleCE = CellEntity(acceleration, collidable, dimension, position, speed, visible, typeEntity)
    val notVisibleCE = CellEntity(acceleration, collidable, dimension1, position1, speed, notVisible, typeEntity)
    EntityManager.add(visibleCE)
    EntityManager.add(notVisibleCE)
    system.update()
    assert(spy.entities.size == 1)
  }

  test("CellEntity enemies correctly wrapped") {
    val spy = DrawSystemSpy()
    val system = DrawSystem(spy)
    val visibleCE = CellEntity(acceleration, collidable, dimension, position, speed, visible, typeEntity)
    val visibleCE1 = CellEntity(acceleration, collidable, dimension1, position1, speed1, visible, typeEntity)
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
