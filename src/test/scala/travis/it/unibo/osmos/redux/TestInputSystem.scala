package it.unibo.osmos.redux

import it.unibo.osmos.redux.ecs.components._
import it.unibo.osmos.redux.ecs.entities.{CellEntity, EntityManager, EntityType, PlayerCellEntity}
import it.unibo.osmos.redux.ecs.systems.InputSystem
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
  val typeEntity = Seq(TypeComponent(EntityType.Matter), TypeComponent(EntityType.Matter), TypeComponent(EntityType.Matter))

  test("InputSystem updates entities acceleration correctly") {

    //setup input system
    val system = InputSystem()

    //add entities to the system using entity manager
    val pce = PlayerCellEntity(acceleration(0), collidable(0), dimension(0), position(0), speed(0), visibility(0), SpawnerComponent(false), typeEntity(0))
    EntityManager.add(pce)

    //prepare list of events to apply
    val events = List(MouseEventWrapper(Point(157,104), pce.getUUID),  MouseEventWrapper(Point(200,194), pce.getUUID), MouseEventWrapper(Point(314,44), pce.getUUID))

    //add mouse events to Input event stack
    InputEventQueue.enqueue(events: _*)

    //pre-compute expected values
    val expectedAccel = computeExpectedAcceleration(system, pce, events: _*)

    //call system update
    system.update()

    assert(pce.getAccelerationComponent == expectedAccel)
  }

  test("InputSystem should update only entities with input property") {

    //setup input system
    val system = InputSystem()

    //add entities to the system using entity manager
    val pce = PlayerCellEntity(acceleration(0), collidable(0), dimension(0), position(0), speed(0), visibility(0), SpawnerComponent(false), typeEntity(0))
    val ce = CellEntity(acceleration(1), collidable(1), dimension(1), position(1), speed(1), visibility(1), typeEntity(1))
    EntityManager.add(pce)
    EntityManager.add(ce)

    //save original acceleration value
    val originalAccel = AccelerationComponent(acceleration(1).vector.x, acceleration(1).vector.y)

    //pre-compute expected values
    val expectedAccel = computeExpectedAcceleration(system, pce, MouseEventWrapper(Point(157,104), pce.getUUID))

    //add mouse event to Input event stack
    InputEventQueue.enqueue(MouseEventWrapper(Point(157,104), pce.getUUID))

    //call system update
    system.update()

    assert(pce.getAccelerationComponent == expectedAccel && ce.getAccelerationComponent == originalAccel)
  }

  def computeExpectedAcceleration(system: InputSystem, entity: PlayerCellEntity, events: MouseEventWrapper*): AccelerationComponent = {
    val pos = entity.getPositionComponent
    val accel = entity.getAccelerationComponent

    var newAccel = AccelerationComponent(accel.vector.x, accel.vector.y)
    events foreach (ev => {
      val p = MathUtils.normalizePoint(Point(pos.point.x - ev.point.x, pos.point.y - ev.point.y))
      newAccel = AccelerationComponent(newAccel.vector.x + p.x * system.accelCoefficient,
        newAccel.vector.y + p.y * system.accelCoefficient)
    })
    newAccel
  }
}
