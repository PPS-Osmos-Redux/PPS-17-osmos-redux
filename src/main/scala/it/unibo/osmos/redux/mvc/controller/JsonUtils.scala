package it.unibo.osmos.redux.mvc.controller

import it.unibo.osmos.redux.ecs.components._
import it.unibo.osmos.redux.ecs.entities.{CellEntity, PlayerCellEntity}
import it.unibo.osmos.redux.utils.Point
import spray.json._
import DefaultJsonProtocol._

object Converters {
  implicit val collidableFormatter: RootJsonFormat[JsCollidable] = jsonFormat1(JsCollidable)
  implicit val dimensionFormatter: RootJsonFormat[JsDimension] = jsonFormat1(JsDimension)
  implicit val pointFormatter: RootJsonFormat[JsPoint] = jsonFormat2(JsPoint)
  implicit val positionFormatter: RootJsonFormat[JsPosition] = jsonFormat1(JsPosition)
  implicit val speedFormatter: RootJsonFormat[JsSpeed] = jsonFormat2(JsSpeed)
  implicit val visibleFormatter: RootJsonFormat[JsVisible] = jsonFormat1(JsVisible)
  implicit val accelerationFormatter: RootJsonFormat[JsAcceleration] = jsonFormat2(JsAcceleration)
  implicit val cellTypeFormatter: RootJsonFormat[JsTypeEntity] = jsonFormat1(JsTypeEntity)
  implicit val cellEntityFormatter:RootJsonFormat[JsCellEntity] = jsonFormat7(JsCellEntity)
  implicit val playerCellEntityFormatter:RootJsonFormat[JsPlayerCellEntity] = jsonFormat7(JsPlayerCellEntity)
}

trait GenericCell

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
                        jsTypeEntity: JsTypeEntity) /*extends GenericCell*/{

  def toCellEntity = CellEntity(acceleration.toAcceleration,
                                collidable.toCollidableComponent,
                                dimension.toDimensionComponent,
                                position.toPosition,
                                speed.toSpeed,
                                visible.toVisible,
                                jsTypeEntity.toEntityType)
}

case class JsPlayerCellEntity(acceleration: JsAcceleration,
                              collidable: JsCollidable,
                              dimension: JsDimension,
                              position: JsPosition,
                              speed: JsSpeed, visible: JsVisible,
                              jsTypeEntity: JsTypeEntity)/* extends GenericCell */{

  def toPlayerCellEntity = PlayerCellEntity(acceleration.toAcceleration,
                                            collidable.toCollidableComponent,
                                            dimension.toDimensionComponent,
                                            position.toPosition,
                                            speed.toSpeed,
                                            visible.toVisible,
                                            jsTypeEntity.toEntityType)
}
