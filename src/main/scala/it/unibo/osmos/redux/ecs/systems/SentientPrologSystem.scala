package it.unibo.osmos.redux.ecs.systems

import alice.tuprolog._
import it.unibo.osmos.redux.ecs.entities.{SentientEnemyProperty, SentientProperty}
import it.unibo.osmos.redux.utils.PrologRules
import it.unibo.osmos.redux.utils.Scala2P._

import scala.collection.mutable.ListBuffer

case class SentientPrologSystem() extends AbstractSystemWithTwoTypeOfEntity[SentientProperty, SentientEnemyProperty]() {

  private val engine: Term => Stream[SolveInfo] = mkPrologEngine(PrologRules.rules)

  override protected def getGroupProperty: Class[SentientProperty] = classOf[SentientProperty]

  override protected def getGroupPropertySecondType: Class[SentientEnemyProperty] = classOf[SentientEnemyProperty]

  override def update(): Unit = {
    //engine("followTarget(1,2,T)") foreach (println(_))
    entities foreach (sentientEntity => {
      //println(sentientEntity)
      computeSentientCellBehaviour(sentientEntity, entitiesSecondType)
    })
  }

  private def computeSentientCellBehaviour(sentientEntity: SentientProperty, sentientEnemies: ListBuffer[SentientEnemyProperty]): Unit = {
    //val sentientRadius: Double = sentientEntity.getDimensionComponent.radius
    //println(sentientRadius)
    val e = sentientEnemies.head
    //val input = new Struct("followTarget", sentientEntity, e, new Var())
    //val input = new Struct("add", "[1,2]", "[1,2]", new Var("A"))
    //val input = new Struct("pow", new Int(3), new Int(3), new Var("A"))
    val input = new Struct("followTarget", "[[10,30],[30,20]]","[[70,80],[40,30]]", new Var("A"))
    engine(input) foreach (println(_))
  }

}
