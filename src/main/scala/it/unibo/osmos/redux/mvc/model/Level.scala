package it.unibo.osmos.redux.mvc.model
import spray.json._
import DefaultJsonProtocol._
import it.unibo.osmos.redux.ecs.entities.CellEntity
import org.apache.commons.lang3.SerializationException

object JsonProtocol2 {

  implicit object MapShapeFormatter extends RootJsonFormat[MapShape] {
    def write(c: MapShape): JsObject = c match {
      case c:Rectangle => JsObject("height" -> JsNumber(c.height),"base" -> JsNumber(c.base))
      case c:Circle => JsObject("radius" -> JsNumber(c.radius))
      case _ => throw new SerializationException("Shape " + c.getClass.getName + " not managed!")
    }

    def read(value: JsValue): MapShape = {
      value.asJsObject.getFields("height", "base", "radius") match {
        case Seq(JsNumber(height), JsNumber(base)) => Rectangle(height.toDouble, base.toDouble)
        case Seq(JsNumber(radius)) => Circle(radius.toDouble)
        case _ => throw DeserializationException("Map shape expected")
      }
    }
  }

}

object CollisionRules extends Enumeration {
  val bouncing: CollisionRules.Value = Value("Bouncing")
  val instantDeath: CollisionRules.Value = Value("Instant_death")
}

object VictoryRules extends Enumeration {
  val becomeTheBiggest: VictoryRules.Value = Value("Become_the_biggest")
}

sealed trait MapShape
case class Rectangle(height:Double, base:Double) extends MapShape
case class Circle(radius:Double) extends MapShape
case class Quad(d:Double, y:Double) extends MapShape


case class LevelMap(mapShape:MapShape, collisionRule:CollisionRules.Value)

case class Level(levelId:Int,
                 levelMap:LevelMap,
                 entities:List[CellEntity],
                 winningRule:VictoryRules.Value,
                 isSimulation:Boolean)
