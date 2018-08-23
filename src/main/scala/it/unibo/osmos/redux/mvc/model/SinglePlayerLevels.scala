package it.unibo.osmos.redux.mvc.model

import scala.collection.mutable

object SinglePlayerLevels {
  private val levels:mutable.ArraySeq[(String,Boolean)] = mutable.ArraySeq("1" -> true, "2" -> false, "3" -> false, "4" -> false)
  def getLevels:List[(String,Boolean)] = levels.clone.toList

  /**
    * Return the last unlocked level.
    * Thtow exception if the levels list is empty
    * @return the last unlocked level
    */
  @throws(classOf[MatchError])
  def toDoLevel:String = {
    @throws(classOf[MatchError])
    def searchLastAvailableLevel(levelsList:mutable.ArraySeq[(String,Boolean)]):Option[String] = levelsList match {
      case (lv:String,av:Boolean)+:(_:String,av2:Boolean)+:_ if av && !av2 => Some(lv)
      case (lv:String,av:Boolean)+:mutable.ArraySeq((lv2:String,av2:Boolean)) => if(av && !av2) Some(lv) else Some (lv2)
      case _+:t => searchLastAvailableLevel(t)
    }
    searchLastAvailableLevel(levels).get
  }

  /**
    * Unlock the next level
    */
  def unlockNextLevel():Unit = levels.filter(lv => !lv._2)
                                     .foreach(level => {
                                       levels.update(levels.indexOf(level), (level._1, true))
                                       return
                                     })

  /**
    * Method for update application user progression.
    * Used for synchronize application and file user progression
    * @param userStat user stat
    */
  def syncWithFile(userStat: UserStat): Unit ={
    levels.foreach(level => {
      if(level._1.equals(userStat.toDoLevel)) return
      unlockNextLevel()
    })
  }

  /**
    * Method for convert current user progression into an object
    * @return UserStat
    */
  def toUserProgression:UserStat = UserStat(toDoLevel)

  /**
    * Modelling user progression and stat
    * @param toDoLevel last unlocked level
    */
  case class UserStat(toDoLevel:String = "1")
}
