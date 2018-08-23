package it.unibo.osmos.redux.mvc.model

import scala.collection.mutable

object SinglePlayerLevels {
  private val levels:mutable.ArraySeq[(String,Boolean)] = mutable.ArraySeq("1" -> true, "2" -> false, "3" -> false, "4" -> false)
  def getLevels:List[(String,Boolean)] = levels.clone.toList
  def unlockNextLevel():Unit = levels.filter(lv => !lv._2)
                                     .foreach(level => {
                                       levels.update(levels.indexOf(level), (level._1, true))
                                       return
                                     })
}
