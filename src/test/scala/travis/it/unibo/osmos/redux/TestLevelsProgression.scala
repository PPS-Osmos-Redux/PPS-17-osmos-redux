package it.unibo.osmos.redux

import it.unibo.osmos.redux.mvc.controller.levels.SinglePlayerLevels
import it.unibo.osmos.redux.mvc.controller.manager.files.{LevelFileManager, UserProgressFileManager}
import it.unibo.osmos.redux.mvc.view.events.{GameLost, GameWon}
import org.scalatest.{BeforeAndAfter, FunSuite}

class TestLevelsProgression extends FunSuite with BeforeAndAfter {

  before(SinglePlayerLevels.init(LevelFileManager.getLevelsConfigResourcesPath().getOrElse(List()).map(fileName => LevelFileManager.getResourceLevelInfo(fileName))))
  after(UserProgressFileManager.deleteUserProgress())

  test("Test complete levels"){
    assert(SinglePlayerLevels.toDoLevel().equals(SinglePlayerLevels.getLevelsInfo.head.name))
    //Win level 1
    SinglePlayerLevels.newEndGameEvent(GameWon, SinglePlayerLevels.getLevelsInfo.head.name)
    assert(SinglePlayerLevels.toDoLevel().equals(SinglePlayerLevels.getLevelsInfo(1).name))
    //Win level 1
    SinglePlayerLevels.newEndGameEvent(GameWon, SinglePlayerLevels.getLevelsInfo.head.name)
    assert(SinglePlayerLevels.toDoLevel().equals(SinglePlayerLevels.getLevelsInfo(1).name))
    //Win level 2
    SinglePlayerLevels.newEndGameEvent(GameWon, SinglePlayerLevels.getLevelsInfo(1).name)
    assert(SinglePlayerLevels.toDoLevel().equals(SinglePlayerLevels.getLevelsInfo(2).name))
    //Lose level 3
    SinglePlayerLevels.newEndGameEvent(GameLost, SinglePlayerLevels.getLevelsInfo(2).name)
    assert(SinglePlayerLevels.toDoLevel().equals(SinglePlayerLevels.getLevelsInfo(2).name))
    //Win level 3
    SinglePlayerLevels.newEndGameEvent(GameWon, SinglePlayerLevels.getLevelsInfo(2).name)
    assert(SinglePlayerLevels.toDoLevel().equals(SinglePlayerLevels.getLevelsInfo(3).name))
    //Win level 4
    SinglePlayerLevels.newEndGameEvent(GameWon, SinglePlayerLevels.getLevelsInfo(3).name)
    assert(SinglePlayerLevels.toDoLevel().equals(SinglePlayerLevels.getLevelsInfo(4).name))
    //Win level 5
    SinglePlayerLevels.newEndGameEvent(GameWon, SinglePlayerLevels.getLevelsInfo(4).name)
    assert(SinglePlayerLevels.toDoLevel().equals(SinglePlayerLevels.getLevelsInfo(4).name))
    //Reset
    SinglePlayerLevels.reset()
    assert(SinglePlayerLevels.toDoLevel().equals(SinglePlayerLevels.getCampaignLevels.head.levelInfo.name))
  }

  test("Test user statistics") {
    assert(SinglePlayerLevels.toDoLevel().equals(SinglePlayerLevels.getLevelsInfo.head.name))

    SinglePlayerLevels.newEndGameEvent(GameWon, SinglePlayerLevels.getLevelsInfo.head.name)
    assert(SinglePlayerLevels.getCampaignLevels.head.levelStat.victories == 1)
    assert(SinglePlayerLevels.getCampaignLevels.head.levelStat.defeats == 0)

    SinglePlayerLevels.newEndGameEvent(GameWon, SinglePlayerLevels.getLevelsInfo(1).name)
    assert(SinglePlayerLevels.getCampaignLevels(1).levelStat.victories == 1)
    assert(SinglePlayerLevels.getCampaignLevels(1).levelStat.defeats == 0)

    SinglePlayerLevels.newEndGameEvent(GameLost, SinglePlayerLevels.getLevelsInfo(2).name)
    assert(SinglePlayerLevels.getCampaignLevels(2).levelStat.victories == 0)
    assert(SinglePlayerLevels.getCampaignLevels(2).levelStat.defeats == 1)

    SinglePlayerLevels.newEndGameEvent(GameWon, SinglePlayerLevels.getLevelsInfo(2).name)
    assert(SinglePlayerLevels.getCampaignLevels(2).levelStat.victories == 1)
    assert(SinglePlayerLevels.getCampaignLevels(2).levelStat.defeats == 1)

    SinglePlayerLevels.newEndGameEvent(GameWon, SinglePlayerLevels.getLevelsInfo(3).name)
    assert(SinglePlayerLevels.getCampaignLevels(3).levelStat.victories == 1)
    assert(SinglePlayerLevels.getCampaignLevels(3).levelStat.defeats == 0)

    SinglePlayerLevels.newEndGameEvent(GameWon, SinglePlayerLevels.getLevelsInfo(4).name)
    assert(SinglePlayerLevels.getCampaignLevels(4).levelStat.victories == 1)
    assert(SinglePlayerLevels.getCampaignLevels(4).levelStat.defeats == 0)
  }
}
