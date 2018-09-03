package it.unibo.osmos.redux.utils

import alice.tuprolog._
import it.unibo.osmos.redux.ecs.entities.composed.SentientProperty
import it.unibo.osmos.redux.ecs.entities.properties.composed.{SentientEnemyProperty, SentientProperty}

import scala.collection.mutable.ListBuffer

object Scala2P {

  def extractTerm(solveInfo: SolveInfo, i: Integer): Term =
    solveInfo.getSolution.asInstanceOf[Struct].getArg(i).getTerm

  def extractTerm(solveInfo: SolveInfo, s: String): Term =
    solveInfo.getTerm(s)

  def separate[E1, E2](element1: E1, element2: E2): String = element1 + "," + element2

  def wrap[E](element: E): String = "[" + element + "]"

  implicit def stringToTerm(s: String): Term = Term.createTerm(s)

  implicit def seqToTerm[T](s: Seq[T]): Term = s.mkString("[", ",", "]")

  implicit def stringToTheory[T](s: String): Theory = new Theory(s)

  implicit def sentientEnemyPropertyToTerm(s: SentientEnemyProperty): Term = {
    val position = s.getPositionComponent.point
    val speed = s.getSpeedComponent.vector
    val positionToTerm = wrap(separate(position.x, position.y))
    val speedToTerm = wrap(separate(speed.x, speed.y))
    val radiusToTerm = s.getDimensionComponent.radius

    val typeToTerm = "'" + s.getTypeComponent.typeEntity + "'"

    wrap(separate(separate(positionToTerm, speedToTerm), separate(radiusToTerm, typeToTerm)))
  }

  implicit def sentientEnemyPropertyListToTerm(sentientEnemies: ListBuffer[SentientEnemyProperty]): Term = {
    if (sentientEnemies.isEmpty) {
      wrap("[]")
    } else {
      var result = ""
      sentientEnemies foreach (enemy => {
        result = separate(result, sentientEnemyPropertyToTerm(enemy))
      })
      // substring removes the first ,
      wrap(result.substring(1))
    }
  }

  implicit def sentientPropertyToTerm(s: SentientProperty): Term = {
    val position = s.getPositionComponent.point
    val speed = s.getSpeedComponent.vector
    val positionToTerm = wrap(separate(position.x, position.y))
    val speedToTerm = wrap(separate(speed.x, speed.y))
    val radiusToTerm = s.getDimensionComponent.radius
    wrap(separate(separate(positionToTerm, speedToTerm), radiusToTerm))
  }

  implicit def sentientPropertyListToTerm(sentientEnemies: ListBuffer[SentientProperty]): Term = {
    if (sentientEnemies.isEmpty) {
      wrap("[]")
    } else {
      var result = ""
      sentientEnemies foreach (enemy => {
        result = separate(result, sentientPropertyToTerm(enemy))
      })
      // substring removes the first ,
      wrap(result.substring(1))
    }
  }

  def mkPrologEngine(theory: Theory): Term => Stream[SolveInfo] = {
    val engine = new Prolog
    engine.setTheory(theory)

    goal =>
      new Iterable[SolveInfo] {

        override def iterator: Iterator[SolveInfo] = new Iterator[SolveInfo] {
          var solution: Option[SolveInfo] = Some(engine.solve(goal))

          override def hasNext: Boolean = solution.isDefined &&
            (solution.get.isSuccess || solution.get.hasOpenAlternatives)

          override def next(): SolveInfo =
            try solution.get
            finally solution = if (solution.get.hasOpenAlternatives) Some(engine.solveNext()) else None
        }
      }.toStream
  }

  def solveWithSuccess(engine: Term => Stream[SolveInfo], goal: Term): Boolean =
    engine(goal).map(_.isSuccess).headOption == Some(true)

  def solveOneAndGetTerm(engine: Term => Stream[SolveInfo], goal: Term, term: String): Term =
    engine(goal).headOption map (extractTerm(_, term)) get
}
