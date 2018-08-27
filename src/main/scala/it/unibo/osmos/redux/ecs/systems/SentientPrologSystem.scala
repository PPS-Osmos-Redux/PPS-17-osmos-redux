package it.unibo.osmos.redux.ecs.systems

import alice.tuprolog._
import it.unibo.osmos.redux.ecs.entities.{SentientEnemyProperty, SentientProperty}
import it.unibo.osmos.redux.utils.Scala2P.{separate, _}
import it.unibo.osmos.redux.utils.{PrologRules, Vector}

case class SentientPrologSystem() extends AbstractSystemWithTwoTypeOfEntity[SentientProperty, SentientEnemyProperty]() {

  private val prologEngine: Term => Stream[SolveInfo] = mkPrologEngine(PrologRules.rules)

  override protected def getGroupProperty: Class[SentientProperty] = classOf[SentientProperty]

  override protected def getGroupPropertySecondType: Class[SentientEnemyProperty] = classOf[SentientEnemyProperty]

  override def update(): Unit = {
    var i = 0
    var result = ""
    if (entities.nonEmpty) {
      entities foreach (_ => {
        result += "," + wrap(separate("AX" + i, "AY" + i))
        i += 1
      })
      result = wrap(result.substring(1))
    } else {
      result = "A"
    }

    val input = new Struct("computeBehaviourOfSentientCells", entities, entitiesSecondType, result)
    prologEngine(input) headOption match {
      case Some(value) =>
        i = 0
        entities foreach (sentientEntity => {
          val sentientCellAccelerationX = value.getTerm("AX" + i).toString.toDouble
          val sentientCellAccelerationY = value.getTerm("AY" + i).toString.toDouble
          // println(sentientCellAccelerationX + " " + sentientCellAccelerationY)
          val computedAcceleration = Vector(sentientCellAccelerationX, sentientCellAccelerationY)
          val acceleration = sentientEntity.getAccelerationComponent
          acceleration.vector_(acceleration.vector add computedAcceleration)
          i += 1
        })
      case _ =>
    }
  }

}
