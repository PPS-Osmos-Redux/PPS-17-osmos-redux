package it.unibo.osmos.redux

import it.unibo.osmos.redux.mvc.model.SinglePlayerLevels
import it.unibo.osmos.redux.mvc.view.events.{GameLost, GameWon}
import org.scalatest.{BeforeAndAfter, FunSuite}

class TestLevelsProgression extends FunSuite with BeforeAndAfter {

  after(SinglePlayerLevels.reset())

  test("Test complete levels"){
    assert(SinglePlayerLevels.toDoLevel.equals(SinglePlayerLevels.getLevels.head.name))

    SinglePlayerLevels.newEndGameEvent(GameWon, SinglePlayerLevels.getLevels.head.name)
    assert(SinglePlayerLevels.toDoLevel.equals(SinglePlayerLevels.getLevels(1).name))

    SinglePlayerLevels.newEndGameEvent(GameWon, SinglePlayerLevels.getLevels(1).name)
    assert(SinglePlayerLevels.toDoLevel.equals(SinglePlayerLevels.getLevels(2).name))

    SinglePlayerLevels.newEndGameEvent(GameLost, SinglePlayerLevels.getLevels(2).name)
    assert(SinglePlayerLevels.toDoLevel.equals(SinglePlayerLevels.getLevels(2).name))

    SinglePlayerLevels.newEndGameEvent(GameWon, SinglePlayerLevels.getLevels(2).name)
    assert(SinglePlayerLevels.toDoLevel.equals(SinglePlayerLevels.getLevels(3).name))

    SinglePlayerLevels.newEndGameEvent(GameWon, SinglePlayerLevels.getLevels(3).name)
    assert(SinglePlayerLevels.toDoLevel.equals(SinglePlayerLevels.getLevels(4).name))

    SinglePlayerLevels.newEndGameEvent(GameWon, SinglePlayerLevels.getLevels(4).name)
    assert(SinglePlayerLevels.toDoLevel.equals(SinglePlayerLevels.getLevels(4).name))
  }

  test("Test user statistics") {
    assert(SinglePlayerLevels.toDoLevel.equals(SinglePlayerLevels.getLevels.head.name))

    SinglePlayerLevels.newEndGameEvent(GameWon, SinglePlayerLevels.getLevels.head.name)
    assert(SinglePlayerLevels.userStatistics().stats.head.victories == 1)
    assert(SinglePlayerLevels.userStatistics().stats.head.defeats == 0)

    SinglePlayerLevels.newEndGameEvent(GameWon, SinglePlayerLevels.getLevels(1).name)
    assert(SinglePlayerLevels.userStatistics().stats(1).victories == 1)
    assert(SinglePlayerLevels.userStatistics().stats(1).defeats == 0)

    SinglePlayerLevels.newEndGameEvent(GameLost, SinglePlayerLevels.getLevels(2).name)
    assert(SinglePlayerLevels.userStatistics().stats(2).victories == 0)
    assert(SinglePlayerLevels.userStatistics().stats(2).defeats == 1)

    SinglePlayerLevels.newEndGameEvent(GameWon, SinglePlayerLevels.getLevels(2).name)
    assert(SinglePlayerLevels.userStatistics().stats(2).victories == 1)
    assert(SinglePlayerLevels.userStatistics().stats(2).defeats == 1)

    SinglePlayerLevels.newEndGameEvent(GameWon, SinglePlayerLevels.getLevels(3).name)
    assert(SinglePlayerLevels.userStatistics().stats(3).victories == 1)
    assert(SinglePlayerLevels.userStatistics().stats(3).defeats == 0)

    SinglePlayerLevels.newEndGameEvent(GameWon, SinglePlayerLevels.getLevels(4).name)
    assert(SinglePlayerLevels.userStatistics().stats(4).victories == 1)
    assert(SinglePlayerLevels.userStatistics().stats(4).defeats == 0)
  }
}
