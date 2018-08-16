package it.unibo.osmos.redux.ecs.components

import scala.collection.mutable

/**
  * Spawner Component
  */
trait SpawnerComponent {

  /**
    * Stack
    */
  protected val actionQueue: mutable.Queue[SpawnAction] = mutable.Queue()

  /**
    * Getter. Defines whether the spawner can spawn new entities or not.
    * @return True, if the spawner can spawn; otherwise false.
    */
  def canSpawn: Boolean

  /**
    * Setter. Defines whether the spawner can spawn new entities or not.
    * @param value True, if the spawner can spawn; otherwise false.
    */
  def canSpawn_(value: Boolean): Unit

  /**
    * Enqueue one or more spawn action.
    * @param actions The spawn actions.
    */
  def enqueueActions(actions: SpawnAction*): Unit

  /**
    * Gets a spawn action to perform.
    * @return The spawn action.
    */
  def dequeueAction(): Option[SpawnAction]

  /**
    * Clears all queued spawn actions.
    */
  def clearActions(): Unit
}

object SpawnerComponent {
  def apply(canSpawn: Boolean): SpawnerComponent = SpawnerComponentImpl(canSpawn)

  private case class SpawnerComponentImpl(var _canSpawn: Boolean, var _spawnableEntities: Int = 0) extends SpawnerComponent {

    override def canSpawn: Boolean = _canSpawn

    override def canSpawn_(canSpawn: Boolean): Unit = _canSpawn = canSpawn

    override def enqueueActions(actions: SpawnAction*): Unit = actionQueue.enqueue(actions: _*)

    override def dequeueAction(): Option[SpawnAction] = if (actionQueue.nonEmpty) Some(actionQueue.dequeue()) else None

    override def clearActions(): Unit = actionQueue.clear()
  }
}

