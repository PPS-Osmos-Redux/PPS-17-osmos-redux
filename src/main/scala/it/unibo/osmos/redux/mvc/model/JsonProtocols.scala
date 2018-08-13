package it.unibo.osmos.redux.mvc.model
import spray.json._
import DefaultJsonProtocol._
import it.unibo.osmos.redux.ecs.components._
import it.unibo.osmos.redux.ecs.entities.{CellEntity, PlayerCellEntity}
import it.unibo.osmos.redux.utils.Point
import spray.json.{DeserializationException, JsBoolean, JsNumber, JsObject, JsString, JsValue, RootJsonFormat}

object JsonProtocols {
  implicit object AccelerationFormatter extends RootJsonFormat[AccelerationComponent] {
    def write(c: AccelerationComponent) = JsObject(
      "accelerationX" -> JsNumber(c.accelerationX),
      "accelerationY" -> JsNumber(c.accelerationY)
    )
    def read(value: JsValue): AccelerationComponent = {
      value.asJsObject.getFields("accelerationX", "accelerationY") match {
        case Seq(JsNumber(accelerationX), JsNumber(accelerationY)) =>
          AccelerationComponent.apply(accelerationX.toDouble,accelerationY.toDouble)
        case _ => throw DeserializationException("Acceleration component expected")
      }
    }
  }

  implicit object CollidableFormatter extends RootJsonFormat[CollidableComponent] {
    def write(c: CollidableComponent) = JsObject("collidable" -> JsBoolean(c.isCollidable()))
    def read(value: JsValue): CollidableComponent = {
      value.asJsObject.getFields("collidable") match {
        case Seq(JsBoolean(collidable)) => CollidableComponent.apply(collidable)
        case _ => throw DeserializationException("Collidable component expected")
      }
    }
  }

  implicit object VisibleFormatter extends RootJsonFormat[VisibleComponent] {
    def write(c: VisibleComponent) = JsObject("visible" -> JsBoolean(c.isVisible()))
    def read(value: JsValue): VisibleComponent = {
      value.asJsObject.getFields("visible") match {
        case Seq(JsBoolean(visible)) => VisibleComponent.apply(visible)
        case _ => throw DeserializationException("Collidable component expected")
      }
    }
  }

  implicit object DimensionFormatter extends RootJsonFormat[DimensionComponent] {
    def write(c: DimensionComponent) = JsObject("radius" -> JsNumber(c.radius))
    def read(value: JsValue): DimensionComponent = {
      value.asJsObject.getFields("radius") match {
        case Seq(JsNumber(radius)) => DimensionComponent.apply(radius.toDouble)
        case _ => throw DeserializationException("Dimension component expected")
      }
    }
  }

  implicit object PointFormatter extends RootJsonFormat[Point] {
    def write(c: Point) = JsObject("x" -> JsNumber(c.x), "y" -> JsNumber(c.y))
    def read(value: JsValue): Point = {
      value.asJsObject.getFields("x","y") match {
        case Seq(JsNumber(x), JsNumber(y)) => Point.apply(x.toDouble,y.toDouble)
        case _ => throw DeserializationException("Point expected")
      }
    }
  }

  implicit object PositionFormatter extends RootJsonFormat[PositionComponent] {
    def write(c: PositionComponent) = JsObject("point" -> c.point.toJson)
    def read(value: JsValue): PositionComponent = {
      value.asJsObject.getFields("point") match {
        case Seq(point) => PositionComponent.apply(point.convertTo[Point])
        case _ => throw DeserializationException("Position component expected")
      }
    }
  }

  implicit object SpeedFormatter extends RootJsonFormat[SpeedComponent] {
    def write(c: SpeedComponent) =
      JsObject("speedX" -> JsNumber(c.speedX), "speedY" -> JsNumber(c.speedY))
    def read(value: JsValue): SpeedComponent = {
      value.asJsObject.getFields("speedX","speedY") match {
        case Seq(JsNumber(speedX), JsNumber(speedY)) =>
          SpeedComponent.apply(speedX.toDouble,speedY.toDouble)
        case _ => throw DeserializationException("Speed component expected")
      }
    }
  }

  implicit object EntityTypeFormatter extends RootJsonFormat[TypeComponent] {
    def write(c: TypeComponent) = JsObject("entity_type" ->  JsString(c.typeEntity.toString))
    def read(value: JsValue): TypeComponent = {
      value.asJsObject.getFields("entity_type") match {
        case Seq(JsString(entityType)) => TypeComponent.apply(EntityType.withName(entityType))
        case _ => throw DeserializationException("Type component component expected")
      }
    }
  }

  implicit object CellEntityFormatter extends RootJsonFormat[CellEntity] {
    def write(c: CellEntity) = JsObject(
      "acceleration" -> c.getAccelerationComponent.toJson,
      "collidable" -> c.getCollidableComponent.toJson,
      "dimension" -> c.getDimensionComponent.toJson,
      "position" -> c.getPositionComponent.toJson,
      "speed" -> c.getSpeedComponent.toJson,
      "visible" -> c.getVisibleComponent.toJson,
      "typeEntity" -> c.getTypeComponent.toJson)
    def read(value: JsValue): CellEntity = {
      value.asJsObject.getFields("acceleration",
        "collidable",
        "dimension",
        "position",
        "speed",
        "visible",
        "typeEntity") match {
        case Seq(acceleration, collidable, dimension, position, speed, visible, typeEntity) =>
          CellEntity.apply(acceleration.convertTo[AccelerationComponent],
            collidable.convertTo[CollidableComponent],
            dimension.convertTo[DimensionComponent],
            position.convertTo[PositionComponent],
            speed.convertTo[SpeedComponent],
            visible.convertTo[VisibleComponent],
            typeEntity.convertTo[TypeComponent])
        case _ => throw DeserializationException("Cell entity expected")
      }
    }
  }

  implicit object PlayerCellEntityFormatter extends RootJsonFormat[PlayerCellEntity] {
    def write(c: PlayerCellEntity) = JsObject(
      "acceleration" -> c.getAccelerationComponent.toJson,
      "collidable" -> c.getCollidableComponent.toJson,
      "dimension" -> c.getDimensionComponent.toJson,
      "position" -> c.getPositionComponent.toJson,
      "speed" -> c.getSpeedComponent.toJson,
      "visible" -> c.getVisibleComponent.toJson,
      "typeEntity" -> c.getTypeComponent.toJson)
    def read(value: JsValue): PlayerCellEntity = {
      value.asJsObject.getFields("acceleration",
        "collidable",
        "dimension",
        "position",
        "speed",
        "visible",
        "typeEntity") match {
        case Seq(acceleration, collidable, dimension, position, speed, visible, typeEntity) =>
          PlayerCellEntity.apply(acceleration.convertTo[AccelerationComponent],
            collidable.convertTo[CollidableComponent],
            dimension.convertTo[DimensionComponent],
            position.convertTo[PositionComponent],
            speed.convertTo[SpeedComponent],
            visible.convertTo[VisibleComponent],
            typeEntity.convertTo[TypeComponent])
        case _ => throw DeserializationException("Player cell entity expected")
      }
    }
  }
}
