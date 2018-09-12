package it.unibo.osmos.redux

import it.unibo.osmos.redux.ecs.entities._
import it.unibo.osmos.redux.mvc.controller.levels.structure.MapShape.{Circle, Rectangle}
import it.unibo.osmos.redux.mvc.controller.levels.structure._
import it.unibo.osmos.redux.mvc.controller.manager.files.{LevelFileManager, SettingsFileManger, UserProgressFileManager}
import it.unibo.osmos.redux.mvc.controller.{Setting, Volume}
import it.unibo.osmos.redux.utils.Point
import org.scalatest.FunSuite

class TestFileOperations extends FunSuite {

  val listCell: List[CellEntity] = List(CellBuilder().buildPlayerEntity(),
                                        CellBuilder().buildCellEntity(),
                                        CellBuilder().buildGravityEntity(),
                                        CellBuilder().buildSentientEntity())
  //LevelMap
  val rectangle: MapShape = Rectangle(Point(0, 0), 50, 50)
  val circle: MapShape = Circle(Point(0, 0), 50)
  val listShape: List[MapShape] = List(rectangle, circle)
  val levelMap: LevelMap = LevelMap(rectangle, CollisionRules.bouncing)
  //Level
  val level: Level = Level(LevelInfo("TestLev3l", VictoryRules.becomeTheBiggest),
    levelMap,
    listCell)

  test("Writing and reading custom level") {
    //Check save on file
    assert(LevelFileManager.saveCustomLevel(level))
    //Check load from file
    val readLevel = LevelFileManager.getCustomLevel(level.levelInfo.name)
    assert(readLevel.isDefined)
    assert(readLevel.get.levelInfo.name.equals(level.levelInfo.name))
    assert(readLevel.get.levelMap.equals(level.levelMap))
    assert(readLevel.get.levelInfo.victoryRule.equals(level.levelInfo.victoryRule))
    assert(readLevel.get.entities.size.equals(level.entities.size))

    val firstFileName = level.levelInfo.name
    //A file with the same name exists so it changes level name adding "1" at the end
    LevelFileManager.saveCustomLevel(level)
    //Reading the second saved level
    val readLevel2 = LevelFileManager.getCustomLevel(level.levelInfo.name)
    assert(readLevel2.isDefined)

    assert(readLevel2.get.levelInfo.name.equals(level.levelInfo.name))
    //Delete second file SinglePlayerLevel1
    LevelFileManager.deleteCustomLevel(level.levelInfo.name)
    assert(LevelFileManager.getCustomLevel(level.levelInfo.name).isEmpty)

    //Delete first file SinglePlayerLevel
    LevelFileManager.deleteCustomLevel(firstFileName)
    assert(LevelFileManager.getCustomLevel(firstFileName).isEmpty)
  }

  test("Writing and reading settings") {
    val settings:List[Setting] = List(Volume(1))

    SettingsFileManger.saveSettings(settings)
    assert(SettingsFileManger.loadSettings().equals(settings))
    assert(SettingsFileManger.deleteSettingsFile().isSuccess)
  }

  test("Writing and reading user progress") {
    val userProgress:List[CampaignLevel] = List(CampaignLevel(LevelInfo("1", VictoryRules.absorbAllOtherPlayers)))
    assert(UserProgressFileManager.saveUserProgress(userProgress))
    assert(UserProgressFileManager.loadUserProgress().equals(userProgress))
    assert(UserProgressFileManager.deleteUserProgress().isSuccess)
  }
}
