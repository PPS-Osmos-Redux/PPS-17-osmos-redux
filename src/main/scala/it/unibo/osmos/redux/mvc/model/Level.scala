package it.unibo.osmos.redux.mvc.model
import spray.json._
import DefaultJsonProtocol._
import it.unibo.osmos.redux.ecs.components._
import it.unibo.osmos.redux.ecs.entities._
import it.unibo.osmos.redux.utils.Point

object CollisionRules extends Enumeration {
  val bouncing: CollisionRules.Value = Value("Bouncing")
  val instantDeath: CollisionRules.Value = Value("Instant_death")
}

object VictoryRules extends Enumeration {
  val becomeTheBiggest: VictoryRules.Value = Value("Become_the_biggest")
}

sealed trait MapShape {
  val mapShape:String
}

object MapShape {
  val rectangle:String = "RECTANGLE"
  val circle:String = "CIRCLE"
  case class Rectangle(height:Double, base:Double) extends MapShape {
    override val mapShape: String = MapShape.rectangle
  }
  case class Circle(radius:Double) extends MapShape {
    override val mapShape: String = MapShape.circle
  }
}

case class LevelMap(mapShape:MapShape, collisionRule:CollisionRules.Value)

case class Level(levelId:Int,
                 levelMap:LevelMap,
                 entities:List[CellEntity],
                 victoryRule:VictoryRules.Value,
                 isSimulation:Boolean)


object deus extends App {
  val a = AccelerationComponent(1, 1)
  val c = CollidableComponent(true)
  val d = DimensionComponent(5)
  val p = PositionComponent(Point(0, 0))
  val s = SpeedComponent(4, 0)
  val v = VisibleComponent(true)
  val et = TypeComponent(EntityType.Material)
  val ce = CellEntity(a, c, d, p, s, v, et)
  val pce = PlayerCellEntity(a,c,d,p,s,v, et)
  val listCell:List[CellEntity] = List(ce, pce)

  val rec:MapShape = MapShape.Rectangle(10.1,5.6)
  val circ:MapShape = MapShape.Circle(5.7)
  val listShape:List[MapShape] = List(rec, circ)

  val levelMap:LevelMap = LevelMap(rec,CollisionRules.bouncing)

  val level:Level = Level(1, levelMap, listCell, VictoryRules.becomeTheBiggest, isSimulation = false)

  import it.unibo.osmos.redux.mvc.model.JsonProtocols._

  val json = level.toJson
  println(json)
  val fromJson = json.convertTo[Level]
  print(fromJson)

}
