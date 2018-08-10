package it.unibo.osmos.redux

import it.unibo.osmos.redux.main.ecs.components._
import it.unibo.osmos.redux.main.ecs.entities.{CellEntity, EntityManager, PlayerCellEntity}
import it.unibo.osmos.redux.main.ecs.systems.InputSystem
import it.unibo.osmos.redux.main.mvc.view.levels.LevelContext
import it.unibo.osmos.redux.main.utils.{InputEventStack, Point}
import org.scalatest.FunSuite
import scalafx.scene.input.{MouseButton, MouseEvent, PickResult}

class TestInputSystem extends FunSuite {

  val acceleration = Seq(AccelerationComponent(1, 1), AccelerationComponent(2, 2), AccelerationComponent(3, 3))
  val collidable = Seq(CollidableComponent(true), CollidableComponent(true), CollidableComponent(false))
  val speed = Seq(SpeedComponent(4, 0), SpeedComponent(2, 0),SpeedComponent(0, 4))
  val dimension = Seq(DimensionComponent(5), DimensionComponent(1), DimensionComponent(8))
  val position = Seq(PositionComponent(Point(0, 0)), PositionComponent(Point(1, 2)), PositionComponent(Point(0, 4)))
  val visibility = Seq(VisibleComponent(true), VisibleComponent(false), VisibleComponent(false))
  val typeEntity = Seq(TypeComponent(EntityType.Material), TypeComponent(EntityType.Material), TypeComponent(EntityType.Material))

  val dummyEvent = new MouseEvent(MouseEvent.MouseClicked, 1.0, 1.0, 1.0, 1.0, MouseButton.Primary, 1,
    false, false, false, false, false, false,
    false, false, false, false, new PickResult(null, 1, 1))

  test("InputSystem priority must match the one passed at his constructor") {
    val priority = 0
    val system = new InputSystem(priority)
    assert(system.priority == priority)
  }

  test("InputSystem updates entities acceleration correctly") {

    //setup level context
    val levelContext = LevelContext(null)
    levelContext.setupLevel()

    //setup input system
    val system = new InputSystem(0)

    //add entities to the system using entity manager
    val pce = PlayerCellEntity(acceleration(0), collidable(0), dimension(0), position(0), speed(0), visibility(0), typeEntity(0))
    EntityManager.add(pce)

    //save original acceleration value
    val originalAccel = AccelerationComponent(acceleration(0).accelerationX, acceleration(0).accelerationY)

    //add mouse event to Input event stack
    InputEventStack.push(dummyEvent)
    InputEventStack.push(dummyEvent)
    InputEventStack.push(dummyEvent)

    //call system update
    system.update()

    assert(pce.getAccelerationComponent.accelerationX == (originalAccel.accelerationX - (system.accelCoefficient*3)) &&
      pce.getAccelerationComponent.accelerationY == (originalAccel.accelerationY - (system.accelCoefficient*3)))
  }

  test("InputSystem should update only entities with input property") {

    //setup level context
    val levelContext = LevelContext(null)
    levelContext.setupLevel()

    //setup input system
    val system = new InputSystem(0)

    //add entities to the system using entity manager
    val pce = PlayerCellEntity(acceleration(0), collidable(0), dimension(0), position(0), speed(0), visibility(0), typeEntity(0))
    val ce = CellEntity(acceleration(1), collidable(1), dimension(1), position(1), speed(1), visibility(1), typeEntity(1))
    EntityManager.add(pce)
    EntityManager.add(ce)

    //save original acceleration value
    val originalAccel0 = AccelerationComponent(acceleration(0).accelerationX, acceleration(0).accelerationY)
    val originalAccel1 = AccelerationComponent(acceleration(1).accelerationX, acceleration(1).accelerationY)

    //add mouse event to Input event stack
    InputEventStack.push(dummyEvent)

    //call system update
    system.update()

    assert(pce.getAccelerationComponent.accelerationX == (originalAccel0.accelerationX - system.accelCoefficient) &&
      pce.getAccelerationComponent.accelerationY == (originalAccel0.accelerationX - system.accelCoefficient) &&
      ce.getAccelerationComponent == originalAccel1)
  }
}
