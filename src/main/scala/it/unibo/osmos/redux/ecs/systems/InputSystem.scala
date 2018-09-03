package it.unibo.osmos.redux.ecs.systems

import it.unibo.osmos.redux.ecs.components.{DimensionComponent, PositionComponent, SpawnAction, SpeedComponent}
import it.unibo.osmos.redux.ecs.entities.InputProperty
import it.unibo.osmos.redux.utils.{InputEventQueue, MathUtils, Point, Vector}

case class InputSystem() extends AbstractSystem[InputProperty] {

  /**
    * Acceleration coefficient to apply to each input movement
    */
  val accelCoefficient: Double = 0.8
  /**
    * The lost mass spawn point offset (starting from the perimeter of the entity, where to spawn lost mass due to movement)
    */
  val lostMassSpawnOffset: Double = 0.1
  /**
    * The amount of lost mass for each movement
    */
  val lostMassPercentage: Double = 0.05
  /**
    * The initial velocity of the lost mass
    */
  val lostMassInitialVelocity: Double = 4.0

  override def update(): Unit = {

    //retrieve all input events
    val inputEvents = InputEventQueue.dequeueAll()

    entities foreach (e => {
      val accel = e.getAccelerationComponent
      val pos = e.getPositionComponent
      val spawner = e.getSpawnerComponent
      val dim = e.getDimensionComponent

      inputEvents filter (_.uuid == e.getUUID) foreach (ev => {

        //TODO: probably at some point there will be different events and it will be necessary to filter them before applying deceleration (es. mouse pressed)

        val newPoint = MathUtils.normalizePoint(Point(pos.point.x - ev.point.x, pos.point.y - ev.point.y))

        //apply acceleration
        //TODO: accel.vector_(accel.vector.add(newPoint.multiply(accelCoefficient)))
        val v = Vector(newPoint.x * accelCoefficient, newPoint.y * accelCoefficient)
        accel.vector_(accel.vector.add(v))
        //accel.vector.x_(accel.vector.x + newPoint.x * accelCoefficient)
        //accel.vector.y_(accel.vector.y + newPoint.y * accelCoefficient)

        //create a new spawn action
        val loseMassAmount = dim.radius * lostMassPercentage
        val directionVector = MathUtils.normalizePoint(Point(ev.point.x - pos.point.x, ev.point.y - pos.point.y))
        val spawnPoint = Point(pos.point.x + directionVector.x * (dim.radius + lostMassSpawnOffset + loseMassAmount),
          pos.point.y + directionVector.y * (dim.radius + lostMassSpawnOffset + loseMassAmount))

        spawner.enqueueActions(SpawnAction(
          PositionComponent(Point(spawnPoint.x, spawnPoint.y)),
          DimensionComponent(loseMassAmount),
          SpeedComponent(directionVector.x * lostMassInitialVelocity, directionVector.y * lostMassInitialVelocity)))

        //make entity lose mass
        dim.radius_(dim.radius - loseMassAmount / 2)
      })
    })
  }
}
