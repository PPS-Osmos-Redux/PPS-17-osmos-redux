package it.unibo.osmos.redux.mvc.model

import scala.collection.mutable

object MultiPlayerLevels {
  private val levels:mutable.ArraySeq[String] = mutable.ArraySeq("1", "2", "3", "4", "5")
  def getLevels:List[String] = levels.clone.toList
}
