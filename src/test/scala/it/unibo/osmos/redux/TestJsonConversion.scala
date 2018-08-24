package it.unibo.osmos.redux

import java.io.File
import java.nio.file.{Files, Path}

import it.unibo.osmos.redux.ecs.components._
import it.unibo.osmos.redux.ecs.entities._
import it.unibo.osmos.redux.mvc.controller.FileManager
import it.unibo.osmos.redux.mvc.controller.FileManager.{defaultFS, levelsDirectory}
import it.unibo.osmos.redux.mvc.model._
import it.unibo.osmos.redux.mvc.model.MapShape._
import it.unibo.osmos.redux.utils.Point
import org.scalatest.FunSuite

import scala.util.Try

class TestJsonConversion extends FunSuite{
  //Components
  val a = AccelerationComponent(1, 1)
  val c = CollidableComponent(true)
  val d = DimensionComponent(5)
  val p = PositionComponent(Point(0, 0))
  val s = SpeedComponent(4, 0)
  val v = VisibleComponent(true)
  val et = TypeComponent(EntityType.Matter)
  val sp = SpawnerComponent(true)
  val sw = SpecificWeightComponent(1)
  //Entities
  val ce = CellEntity(a, c, d, p, s, v, et)
  val pce = PlayerCellEntity(a, c, d, p, s, v, et, sp)
  val gc = GravityCellEntity(a, c, d, p, s, v, et, sw)
  val sc = SentientCellEntity(a, c, d, p, s, v)
  val listCell:List[CellEntity] = List(ce, pce, gc, sc)
  //LevelMap
  val rectangle:MapShape = Rectangle((50,50),10.1,5.6)
  val circle:MapShape = Circle((50.1,50.2), 5.7)
  val listShape:List[MapShape] = List(rectangle, circle)
  val levelMap:LevelMap = LevelMap(rectangle, CollisionRules.bouncing)
  //Level
  val level:Level = Level("1",
    levelMap,
    listCell,
    VictoryRules.becomeTheBiggest)

  import spray.json._
  import DefaultJsonProtocol._
  import it.unibo.osmos.redux.mvc.model.JsonProtocols._

  test("Components conversion") {
    val jsAcceleration = a.toJson
    assert(jsAcceleration.convertTo[AccelerationComponent].equals(a))
    val jsCollidable = c.toJson
    assert(jsCollidable.convertTo[CollidableComponent].equals(c))
    val jsSpeed = s.toJson
    assert(jsSpeed.convertTo[SpeedComponent].equals(s))
    val jsDimension = d.toJson
    assert(jsDimension.convertTo[DimensionComponent].equals(d))
    val jsPosition = p.toJson
    assert(jsPosition.convertTo[PositionComponent].equals(p))
    val jsVisible = v.toJson
    assert(jsVisible.convertTo[VisibleComponent].equals(v))
    val jsEntityType = et.toJson
    assert(jsEntityType.convertTo[TypeComponent].equals(et))
    val jsSpecificWeight = sw.toJson
    assert(jsSpecificWeight.convertTo[SpecificWeightComponent].equals(sw))
  }

  test("Cell Entity conversion") {
    val jsCellEntity = ce.toJson
    assert(jsCellEntity.convertTo[CellEntity].equals(ce))
  }

  test("Player Cell Entity conversion"){
    val jsPlayerCellEntity = pce.toJson
    assert(jsPlayerCellEntity.convertTo[PlayerCellEntity].equals(pce))
  }

  test("Gravity Cell Entity conversion"){
    val jsGravityCell = gc.toJson
    assert(jsGravityCell.convertTo[GravityCellEntity].equals(gc))
  }

  test("Sentient Cell Entity conversion") {
    val jsSentientCell = sc.toJson
    assert(jsSentientCell.convertTo[SentientCellEntity].equals(sc))
  }

  test("List of CellEntity conversion") {
    val jsListCellEntities = listCell.toJson
    val convertedCellList = jsListCellEntities.convertTo[List[CellEntity]]
    //Check covnerted list size
    assert(convertedCellList.size == listCell.size)
    //Check if the second cell is a player cell
    assert(convertedCellList(1).getClass.equals(pce.getClass))
    assert(!convertedCellList.head.getClass.equals(pce.getClass))
    //Check if the third cell is a gravity cell
    assert(convertedCellList(2).getClass.equals(gc.getClass))
    assert(!convertedCellList.head.getClass.equals(gc.getClass))
    //Check if the fourth cell is a sentient cell
    assert(convertedCellList(3).getClass.equals(sc.getClass))
    assert(!convertedCellList.head.getClass.equals(gc.getClass))
  }

  test("Map conversion") {
    val jsRectangle = rectangle.toJson
    assert(jsRectangle.convertTo[MapShape].asInstanceOf[Rectangle].equals(rectangle))
    val jsCircle = circle.toJson
    assert(jsCircle.convertTo[MapShape].asInstanceOf[Circle].equals(circle))
    val jsListOfShape = listShape.toJson
    assert(jsListOfShape.convertTo[List[MapShape]].equals(listShape))
    val jsLevelMap = levelMap.toJson
    assert(jsLevelMap.convertTo[LevelMap].equals(levelMap))
  }

  test("Level conversion") {
    val jsLevel = level.toJson
    val convertedLevel = jsLevel.convertTo[Level]
    assert(convertedLevel.levelId.equals(level.levelId))
    assert(convertedLevel.levelMap.equals(level.levelMap))
    assert(convertedLevel.victoryRule.equals(level.victoryRule))
    assert(convertedLevel.entities.size.equals(level.entities.size))
  }

  test("File reading and conversion (SinglePlayer + MultiPlayer)") {
    val spConvertedLevel = FileManager.loadResource(1.toString)
    assert(spConvertedLevel.isDefined)
//    assert(spConvertedLevel.get.levelId.equals(level.levelId))
//    assert(spConvertedLevel.get.levelMap.equals(level.levelMap))
//    assert(spConvertedLevel.get.victoryRule.equals(level.victoryRule))
//    assert(spConvertedLevel.get.entities.size.equals(level.entities.size))
    val mpConvertedLevel = FileManager.loadResource(4.toString)
    assert(mpConvertedLevel.isDefined)
//    assert(mpConvertedLevel.get.levelId.equals(level.levelId))
//    assert(mpConvertedLevel.get.levelMap.equals(level.levelMap))
//    assert(mpConvertedLevel.get.victoryRule.equals(level.victoryRule))
//    assert(mpConvertedLevel.get.entities.size.equals(level.entities.size))
  }

  test("Writing and reading custom level") {
    val fileName:String = "TestWritingLev3l"
    val optFilePath = FileManager.saveLevel(level, fileName)
    assert(optFilePath.isDefined)
    val readLevel = FileManager.loadCustomLevel(fileName)
    assert(readLevel.isDefined)
    assert(readLevel.get.levelId.equals(level.levelId))
    assert(readLevel.get.levelMap.equals(level.levelMap))
    assert(readLevel.get.victoryRule.equals(level.victoryRule))
    assert(readLevel.get.entities.size.equals(level.entities.size))
    Try(Files.delete(optFilePath.get))
  }
}
