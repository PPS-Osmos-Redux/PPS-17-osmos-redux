package it.unibo.osmos.redux.main.mvc.controller
import spray.json._
import DefaultJsonProtocol._

import scala.io.Source
import it.unibo.osmos.redux.main.ecs.components.{EntityType, _}
import it.unibo.osmos.redux.main.ecs.entities.CellEntity
import it.unibo.osmos.redux.main.mvc.view.levels.LevelContext
import it.unibo.osmos.redux.main.utils.Point

/**
  * Controller base trait
  */
trait Controller {
  def startLevel(levelContext:LevelContext)
}


case class ControllerImpl() extends Controller {
  /*val engine:Engine*/
  override def startLevel(levelContext: LevelContext): Unit = {
    //1) load files
    //val entities = loadEntities()
    //2) call init
    /*if(engine.isEmpty){
      engine = new Engine()
    }
    engine.init(levelContext,entities)*/
    //3) call start
    /*engine.start()*/
  }

//  def loadEntities():List[CellEntity] = {
//    val a = AccelerationComponent(1, 1)
//    val c = CollidableComponent(true)
//    val d = DimensionComponent(10)
//    val dPlayer = DimensionComponent(20)
//    val p = PositionComponent(Point(20, 20))
//    val pPlayer = PositionComponent(Point(50, 50))
//    val s = SpeedComponent(4, 0)
//    val v = VisibleComponent(true)
//    val ce = CellEntity(a, c, d, p, s, v)
//    val pce = PlayerCellEntity(a,c,dPlayer,pPlayer,s,v)
//    List(ce,pce)
//  }
}

object fileReader {
  /**
    * Reads a file from the resources folder
    * @param filename the name of the file
    * @return Content of the file
    */
  def loadResource(filename: String): String = {
    val source = Source.fromURL(getClass.getResource(filename))
    try source.mkString finally source.close()
  }
}

object Translators {
  implicit val collidableFormatter: RootJsonFormat[JsCollidable] = jsonFormat1(JsCollidable)
  implicit val dimensionFormatter: RootJsonFormat[JsDimension] = jsonFormat1(JsDimension)
  implicit val pointFormatter: RootJsonFormat[JsPoint] = jsonFormat2(JsPoint)
  implicit val positionFormatter: RootJsonFormat[JsPosition] = jsonFormat1(JsPosition)
  implicit val speedFormatter: RootJsonFormat[JsSpeed] = jsonFormat2(JsSpeed)
  implicit val visibleFormatter: RootJsonFormat[JsVisible] = jsonFormat1(JsVisible)
  implicit val accelerationFormatter: RootJsonFormat[JsAcceleration] = jsonFormat2(JsAcceleration)
  implicit val cellTypeFormatter: RootJsonFormat[JsTypeEntity] = jsonFormat1(JsTypeEntity)
  implicit val cellEntityFormatter:RootJsonFormat[JsCellEntity] = jsonFormat7(JsCellEntity)
}

case class JsAcceleration(accelerationX: Int, accelerationY: Int) {
  def toAcceleration = AccelerationComponent(accelerationX, accelerationY)
}
case class JsCollidable(collidable: Boolean) {
  def toCollidableComponent = CollidableComponent(collidable)
}

case class JsDimension(radius: Int) {
  def toDimensionComponent = DimensionComponent(radius)
}

case class JsPoint(x: Double, y: Double){
  def toPoint = Point(x,y)
}

case class JsPosition(point: JsPoint){
  def toPosition = PositionComponent(point.toPoint)
}

case class JsSpeed(speedX: Int, speedY: Int){
  def toSpeed = SpeedComponent(speedX,speedY)
}

case class JsVisible(visible: Boolean){
  def toVisible = VisibleComponent(visible)
}

case class JsTypeEntity(typeEntity: String){
  def toEntityType = TypeComponent(EntityType.withName(typeEntity))
}

case class JsCellEntity(acceleration: JsAcceleration,
                        collidable: JsCollidable,
                        dimension: JsDimension,
                        position: JsPosition,
                        speed: JsSpeed, visible: JsVisible,
                        jsTypeEntity: JsTypeEntity) {

  def toCellEntity = CellEntity(acceleration.toAcceleration,
                                collidable.toCollidableComponent,
                                dimension.toDimensionComponent,
                                position.toPosition,
                                speed.toSpeed,
                                visible.toVisible,
                                jsTypeEntity.toEntityType)
}

//object app extends App {
//  val a = JsAcceleration(1, 1)
//  val c = JsCollidable(true)
//  val d = JsDimension(5)
//  val p = JsPosition(JsPoint(0, 0))
//  val s = JsSpeed(4, 0)
//  val v = JsVisible(true)
//  val ct = JsTypeEntity("Material")
//  val ce = JsCellEntity(a, c, d, p, s, v, ct)
//  //val pce = PlayerCellEntity(a,c,d,p,s,v)
//
//  import Translators._
//  val cellToJson = ce.toJson
//  println(cellToJson)
//  val cellFromJson = cellToJson.convertTo[JsCellEntity]
//  println(cellFromJson)
//  println(cellFromJson.toCellEntity)
//}
