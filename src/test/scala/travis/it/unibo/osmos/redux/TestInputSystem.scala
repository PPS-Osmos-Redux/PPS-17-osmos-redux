package it.unibo.osmos.redux

import it.unibo.osmos.redux.ecs.components._
import it.unibo.osmos.redux.ecs.entities.{CellBuilder, EntityManager, PlayerCellEntity}
import it.unibo.osmos.redux.ecs.systems.InputSystem
import it.unibo.osmos.redux.mvc.view.events.MouseEventWrapper
import it.unibo.osmos.redux.utils.{InputEventQueue, MathUtils, Point}
import org.scalatest.{BeforeAndAfter, FunSuite}

class TestInputSystem extends FunSuite with BeforeAndAfter {

  val accelerationCoefficient: Double = 0.8
  var system: InputSystem = _
  var pce: PlayerCellEntity = _

  before{
    system = InputSystem()
    pce = CellBuilder()
      .withAcceleration(1, 1).withSpeed(4, 0)
      .withDimension(5).withPosition(0, 0)
      .buildPlayerEntity()
  }

  after(EntityManager.clear())

  test("InputSystem updates entities acceleration correctly") {

    //add entities to the system using entity manager
    EntityManager.add(pce)

    //prepare list of events to apply
    val events = List(MouseEventWrapper(Point(157, 104), pce.getUUID), MouseEventWrapper(Point(200, 194), pce.getUUID), MouseEventWrapper(Point(314, 44), pce.getUUID))

    //add mouse events to Input event stack
    InputEventQueue.enqueue(events: _*)

    //pre-compute expected values
    val expectedAccel = computeExpectedAcceleration(system, pce, events: _*)

    //call system update
    system.update()

    assert(pce.getAccelerationComponent == expectedAccel)
  }

  test("InputSystem should update only entities with input property") {

    //add entities to the system using entity manager
    val ce = CellBuilder().buildCellEntity()
    EntityManager.add(pce)
    EntityManager.add(ce)

    //save original acceleration value
    val originalAccel =ce.getAccelerationComponent.copy()

    //pre-compute expected values
    val expectedAccel = computeExpectedAcceleration(system, pce, MouseEventWrapper(Point(157, 104), pce.getUUID))

    //add mouse event to Input event stack
    InputEventQueue.enqueue(MouseEventWrapper(Point(157, 104), pce.getUUID))

    //call system update
    system.update()

    assert(pce.getAccelerationComponent == expectedAccel && ce.getAccelerationComponent == originalAccel)
  }

  def computeExpectedAcceleration(system: InputSystem, entity: PlayerCellEntity, events: MouseEventWrapper*): AccelerationComponent = {
    val pos = entity.getPositionComponent
    val accel = entity.getAccelerationComponent

    var newAccel = accel.copy()
    events foreach (ev => {
      val p = MathUtils.unitVector(pos.point, ev.point)
      newAccel = AccelerationComponent(newAccel.vector add (p multiply accelerationCoefficient))
    })
    newAccel
  }
}
