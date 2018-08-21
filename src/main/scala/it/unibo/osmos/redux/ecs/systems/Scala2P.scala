package it.unibo.osmos.redux.ecs.systems

import alice.tuprolog._

object Scala2P {

  def extractTerm(t:Term, i:Integer): Term =
    t.asInstanceOf[Struct].getArg(i).getTerm

  implicit def stringToTerm(s: String): Term = Term.createTerm(s)
  implicit def seqToTerm[T](s: Seq[T]): Term = s.mkString("[",",","]")

  def mkPrologEngine(clauses: String*): Term => Stream[Term] = {
    goal => new Iterable[Term]{
      val engine = new Prolog
      engine.setTheory(new Theory(clauses mkString " "))

      override def iterator = new Iterator[Term]{
        var solution = engine.solve(goal);

        override def hasNext = solution.isSuccess ||
          solution.hasOpenAlternatives

        override def next() =
          try solution.getSolution finally solution = engine.solveNext()
      }
    }.toStream
  }
}
