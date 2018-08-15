package it.unibo.osmos.redux

import it.unibo.osmos.redux.ecs.components._
import it.unibo.osmos.redux.ecs.entities.{CellEntity, EntityManager, PlayerCellEntity}
import it.unibo.osmos.redux.ecs.systems.InputSystem
import it.unibo.osmos.redux.mvc.view.levels.LevelContext
import it.unibo.osmos.redux.mvc.view.events.MouseEventWrapper
import it.unibo.osmos.redux.utils.{InputEventQueue, MathUtils, Point}
import org.scalatest.FunSuite

class TestInputSystem extends FunSuite {

  val acceleration = Seq(AccelerationComponent(1, 1), AccelerationComponent(2, 2), AccelerationComponent(3, 3))
  val collidable = Seq(CollidableComponent(true), CollidableComponent(true), CollidableComponent(false))
  val speed = Seq(SpeedComponent(4, 0), SpeedComponent(2, 0),SpeedComponent(0, 4))
  val dimension = Seq(DimensionComponent(5), DimensionComponent(1), DimensionComponent(8))
  val position = Seq(PositionComponent(Point(0, 0)), PositionComponent(Point(1, 2)), PositionComponent(Point(0, 4)))
  val visibility = Seq(VisibleComponent(true), VisibleComponent(false), VisibleComponent(false))
  val typeEntity = Seq(TypeComponent(EntityType.Material), TypeComponent(EntityType.Material), TypeComponent(EntityType.Material))

  val dummyEvent = MouseEventWrapper(Point(157,104))

  test("InputSystem priority must match the one passed at his constructor") {
    val priority = 0
    val system = InputSystem(priority)
    assert(system.priority == priority)
  }

  test("InputSystem updates entities acceleration correctly") {

    //setup level context
    val levelContext = LevelContext(null,true)
    levelContext.setupLevel()

    //setup input system
    val system = InputSystem(0)

    //add entities to the system using entity manager
    val pce = PlayerCellEntity(acceleration(0), collidable(0), dimension(0), position(0), speed(0), visibility(0), typeEntity(0), SpawnerComponent(false))
    EntityManager.add(pce)

    //prepare list of events to apply
    val events = List(dummyEvent,  MouseEventWrapper(Point(200,194)), MouseEventWrapper(Point(314,44)))

    //add mouse events to Input event stack
    InputEventQueue.enqueue(events: _*)

    //pre-compute expected values
    val expectedAccel = computeExpectedAcceleration(system, pce, events: _*)

    //call system update
    system.update()

    assert(pce.getAccelerationComponent == expectedAccel)
  }

  test("InputSystem should update only entities with input property") {

    //setup level context
    val levelContext = LevelContext(null,true)
    levelContext.setupLevel()

    //setup input system
    val system = InputSystem(0)

    //add entities to the system using entity manager
    val pce = PlayerCellEntity(acceleration(0), collidable(0), dimension(0), position(0), speed(0), visibility(0), typeEntity(0), SpawnerComponent(false))
    val ce = CellEntity(acceleration(1), collidable(1), dimension(1), position(1), speed(1), visibility(1), typeEntity(1))
    EntityManager.add(pce)
    EntityManager.add(ce)

    //save original acceleration value
    val originalAccel = AccelerationComponent(acceleration(1).accelerationX, acceleration(1).accelerationY)

    //pre-compute expected values
    val expectedAccel = computeExpectedAcceleration(system, pce, dummyEvent)

    //add mouse event to Input event stack
    InputEventQueue.enqueue(dummyEvent)

    //call system update
    system.update()

    assert(pce.getAccelerationComponent == expectedAccel && ce.getAccelerationComponent == originalAccel)
  }

  def computeExpectedAcceleration(system: InputSystem, entity: PlayerCellEntity, events: MouseEventWrapper*): AccelerationComponent = {
    val pos = entity.getPositionComponent
    val accel = entity.getAccelerationComponent

    var newAccel = AccelerationComponent(accel.accelerationX, accel.accelerationY)
    events foreach (ev => {
      val p = MathUtils.normalizePoint(Point(pos.point.x - ev.point.x, pos.point.y - ev.point.y))
      newAccel = AccelerationComponent(newAccel.accelerationX + p.x * system.accelCoefficient,
        newAccel.accelerationY + p.y * system.accelCoefficient)
    })
    newAccel
  }
}
