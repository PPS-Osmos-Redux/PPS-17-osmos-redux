package it.unibo.osmos.redux.mvc.controller.levels.structure

/** Level information
  *
  * @param name level name
  * @param victoryRule level victory rule
  * @param isAvailable level is available
  */
case class LevelInfo(var name: String, victoryRule: VictoryRules.Value,  var isAvailable: Boolean = true) {
  /** Set level availability
    *
    * @param flag true if is available
    * @return LevelInfo
    */
  def changeAvailability(flag:Boolean = false): LevelInfo = {
    isAvailable = flag
    this
  }
}
