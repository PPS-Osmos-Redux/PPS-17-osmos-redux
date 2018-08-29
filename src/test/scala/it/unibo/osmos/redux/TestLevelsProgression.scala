package it.unibo.osmos.redux

import it.unibo.osmos.redux.mvc.model.SinglePlayerLevels
import it.unibo.osmos.redux.mvc.view.events.GameWon
import org.scalatest.FunSuite

class TestLevelsProgression extends FunSuite {
  test("Test complete levels"){
    assert(SinglePlayerLevels.toDoLevel.equals(SinglePlayerLevels.getLevels.head.name))

    SinglePlayerLevels.newEndGameEvent(GameWon, SinglePlayerLevels.getLevels.head.name)
    assert(SinglePlayerLevels.toDoLevel.equals(SinglePlayerLevels.getLevels(1).name))

    SinglePlayerLevels.newEndGameEvent(GameWon, SinglePlayerLevels.getLevels(1).name)
    assert(SinglePlayerLevels.toDoLevel.equals(SinglePlayerLevels.getLevels(2).name))

    SinglePlayerLevels.newEndGameEvent(GameWon, SinglePlayerLevels.getLevels(2).name)
    assert(SinglePlayerLevels.toDoLevel.equals(SinglePlayerLevels.getLevels(3).name))

    SinglePlayerLevels.newEndGameEvent(GameWon, SinglePlayerLevels.getLevels(3).name)
    assert(SinglePlayerLevels.toDoLevel.equals(SinglePlayerLevels.getLevels(4).name))

    SinglePlayerLevels.newEndGameEvent(GameWon, SinglePlayerLevels.getLevels(4).name)
    assert(SinglePlayerLevels.toDoLevel.equals(SinglePlayerLevels.getLevels(4).name))
  }
}
