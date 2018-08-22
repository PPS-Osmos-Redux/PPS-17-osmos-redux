package it.unibo.osmos.redux.ecs.systems

import alice.tuprolog.{SolveInfo, Struct, Term}
import it.unibo.osmos.redux.ecs.entities.{SentientEnemyProperty, SentientProperty}
import it.unibo.osmos.redux.utils.Scala2P._

import scala.collection.mutable.ListBuffer

case class SentientPrologSystem() extends AbstractSystemWithTwoTypeOfEntity[SentientProperty, SentientEnemyProperty]() {

  private val engine: Term => Stream[SolveInfo] = mkPrologEngine(
    """
       followTarget(S,T,2).
       followTarget(S,T,3).
       member([H|T],H,T).
       member([H|T],E,[H|T2]):- member(T,E,T2).
       permutation([],[]).
       permutation(L,[H|TP]) :- member(L,H,T), permutation(T,TP).
 """)

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
    val sentientRadius: Double = sentientEntity.getDimensionComponent.radius
    //println(sentientRadius)

    var v = "" + sentientEntity
    /*sentientEnemies.foreach(e => {
      v = v + "," + e
    })*/
    //val t = new Struct(new alice.tuprolog.Int(2), new alice.tuprolog.Int(3))
    //val app = new Struct("permutation", t, new alice.tuprolog.Var())
    //engine(app) foreach (println(_))
    engine("followTarget(1,2,T)") foreach (println(_))

    //engine("permutation([1,2,3],L)") foreach (println(_))
    //var list2=new Struct(new Term[]{new alice.tuprolog.Int(2),new  alice.tuprolog.Int(3)});
  }

}
