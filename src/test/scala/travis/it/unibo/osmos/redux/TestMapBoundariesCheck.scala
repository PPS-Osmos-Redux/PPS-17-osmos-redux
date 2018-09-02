package it.unibo.osmos.redux

import it.unibo.osmos.redux.ecs.components._
import it.unibo.osmos.redux.ecs.entities._
import it.unibo.osmos.redux.mvc.model.MapShape.{Circle, Rectangle}
import it.unibo.osmos.redux.mvc.model._
import it.unibo.osmos.redux.utils.Point
import org.scalatest.FunSuite

class TestMapBoundariesCheck extends FunSuite{
  val a = AccelerationComponent(1, 1)
  val c = CollidableComponent(true)
  val d = DimensionComponent(2)
  val s = SpeedComponent(4, 0)
  val v = VisibleComponent(true)
  val et = TypeComponent(EntityType.Matter)
  val etg = TypeComponent(EntityType.Attractive)
  val sp = SpawnerComponent(true)
  val sw = SpecificWeightComponent(1)
  val spawner = SpawnerComponent(false)

  val levelId:String = 1.toString

  test("Rectangular map boundaries") {
    val p = PositionComponent(Point(-3, -3))
    val p1 = PositionComponent(Point(3, -3))
    val p2= PositionComponent(Point(-3,3))
    val p3 = PositionComponent(Point(3, 3))

    //Entities
    val ce = CellEntity(a, c, d, p, s, v, et)
    val pce = PlayerCellEntity(a, c, d, p1, s, v, et, sp)
    val gc = GravityCellEntity(a, c, d, p2, s, v, etg, sw)
    val sc = SentientCellEntity(a, c, d, p3, s, v, spawner)
    val listCells = List(ce, pce, gc, sc)
    //LevelMap
    val rectangle:MapShape = Rectangle((0,0),10,10)
    val levelMap:LevelMap = LevelMap(rectangle, CollisionRules.bouncing)
    //Level
    var level:Level = Level(levelId,
                            levelMap,
                            listCells,
                            VictoryRules.becomeTheBiggest)
    level.checkCellPosition()
    assert(listCells.size.equals(level.entities.size))

    ce.getPositionComponent.point_(Point(-4, -4))
    pce.getPositionComponent.point_(Point(5, -5))
    gc.getPositionComponent.point_(Point(0,12))
    sc.getPositionComponent.point_(Point(12, 0))
    level = Level(levelId, levelMap, List(ce,pce,gc,sc), VictoryRules.becomeTheBiggest)
    level.checkCellPosition()
    assert(level.entities.isEmpty)
  }

  test("Circular map boundaries") {
    val p = PositionComponent(Point(-3, -3))
    val p1 = PositionComponent(Point(3, -3))
    val p2= PositionComponent(Point(-3,3))
    val p3 = PositionComponent(Point(3, 3))

    //Entities
    val ce = CellEntity(a, c, d, p, s, v, et)
    val pce = PlayerCellEntity(a, c, d, p1, s, v, et, sp)
    val gc = GravityCellEntity(a, c, d, p2, s, v, etg, sw)
    val sc = SentientCellEntity(a, c, d, p3, s, v, spawner)
    val listCells = List(ce, pce, gc, sc)
    //LevelMap
    val circle:MapShape = Circle((0,0), 5)
    val levelMap:LevelMap = LevelMap(circle, CollisionRules.bouncing)
    //Level
    var level:Level = Level(levelId,
                            levelMap,
                            listCells,
                            VictoryRules.becomeTheBiggest)
    level.checkCellPosition()
    assert(level.entities.isEmpty)

    ce.getPositionComponent.point_(Point(-2, 2))
    pce.getPositionComponent.point_(Point(2, 2))
    gc.getPositionComponent.point_(Point(2,-2))
    sc.getPositionComponent.point_(Point(-2, -2))
    level = Level(levelId, levelMap, List(ce,pce,gc,sc), VictoryRules.becomeTheBiggest)
    level.checkCellPosition()
    assert(level.entities.size.equals(listCells.size))
  }
}
