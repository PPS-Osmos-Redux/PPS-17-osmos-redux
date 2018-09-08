package it.unibo.osmos.redux.mvc.controller.levels.structure


/**Level victory rules*/
object VictoryRules extends Enumeration {
  val becomeTheBiggest: VictoryRules.Value = Value("Become the biggest")
  val becomeHuge: VictoryRules.Value = Value("Become huge")
  val absorbTheRepulsors: VictoryRules.Value = Value("Absorb the repulsors")
  val absorbTheHostileCells: VictoryRules.Value = Value("Absorb the hostile cells")
  val absorbAllOtherPlayers: VictoryRules.Value = Value("Absorb all other players")
}
