package it.unibo.osmos.redux.mvc.controller.levels.structure


/**
  * Level victory rules
  */
object VictoryRules extends Enumeration {
  val becomeTheBiggest: VictoryRules.Value = Value("Become_the_biggest")
  val becomeHuge: VictoryRules.Value = Value("Become_huge")
  val absorbTheRepulsors: VictoryRules.Value = Value("Absorb_the_repulsors")
  val absorbTheHostileCells: VictoryRules.Value = Value("Absorb_the_hostile_cells")
  val absorbAllOtherPlayers: VictoryRules.Value = Value("Absorb_all_other_players")
}
