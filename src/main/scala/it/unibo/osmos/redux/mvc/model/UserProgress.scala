package it.unibo.osmos.redux.mvc.model

object UserProgress {
  private var usrProgress:Option[UserStat] = None

  def synchronizeWithFile(userProgress:UserStat): Unit = usrProgress = Some(userProgress)

  def getUserProgress:Option[UserStat] = usrProgress

  case class UserStat(toDoLevel:Int = 1)
}
