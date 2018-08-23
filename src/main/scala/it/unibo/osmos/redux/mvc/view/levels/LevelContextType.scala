package it.unibo.osmos.redux.mvc.view.levels

/**
  * Enumeration representing the type of level the user is playing
  */
object LevelContextType extends Enumeration {

  /**
    * The level is played in normal (campaign) mode
    */
  val normal: LevelContextType.Value = Value(0, "Normal")

  /**
    * The level is played in simulation mode
    */
  val simulation: LevelContextType.Value = Value(1, "Simulation")

  /**
    * The level is played in multiplayer mode
    */
  val multiplayer: LevelContextType.Value = Value(2, "Multiplayer")
}
