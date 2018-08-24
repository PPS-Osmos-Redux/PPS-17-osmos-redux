package it.unibo.osmos.redux.ecs.systems

import alice.tuprolog._
import it.unibo.osmos.redux.ecs.entities.{SentientEnemyProperty, SentientProperty}
import it.unibo.osmos.redux.utils.PrologRules
import it.unibo.osmos.redux.utils.Scala2P._
import it.unibo.osmos.redux.utils.Vector

case class SentientPrologSystem() extends AbstractSystemWithTwoTypeOfEntity[SentientProperty, SentientEnemyProperty]() {

  private val prologEngine: Term => Stream[SolveInfo] = mkPrologEngine(PrologRules.rules)

  override protected def getGroupProperty: Class[SentientProperty] = classOf[SentientProperty]

  override protected def getGroupPropertySecondType: Class[SentientEnemyProperty] = classOf[SentientEnemyProperty]

  override def update(): Unit = {
    entities foreach (sentientEntity => {
      val input = new Struct("sentientCellBehaviour", sentientEntity, sentientEnemiesToTerm(entitiesSecondType), "[RX,RY]")
      prologEngine(input) headOption match {
        case Some(value) =>
          val sentientCellAccelerationX = value.getTerm("RX").toString.toDouble
          val sentientCellAccelerationY = value.getTerm("RY").toString.toDouble
          // println(sentientCellAccelerationX + " " + sentientCellAccelerationY)
          val computedAcceleration = Vector(sentientCellAccelerationX, sentientCellAccelerationY)
          val acceleration = sentientEntity.getAccelerationComponent
          acceleration.vector_(acceleration.vector add computedAcceleration)
        case _ =>
      }
    })
  }

}
