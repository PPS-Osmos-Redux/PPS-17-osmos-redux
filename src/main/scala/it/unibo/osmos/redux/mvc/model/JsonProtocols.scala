package it.unibo.osmos.redux.mvc.model
import spray.json._
import DefaultJsonProtocol._
import it.unibo.osmos.redux.ecs.components._
import it.unibo.osmos.redux.ecs.entities.{CellEntity, PlayerCellEntity}
import it.unibo.osmos.redux.utils.Point
import org.apache.commons.lang3.SerializationException

/**
  * Json implicit stategies for: convert json to Level or convert Level to json
  */
object JsonProtocols {
  implicit object AccelerationFormatter extends RootJsonFormat[AccelerationComponent] {
    def write(acceleration: AccelerationComponent) = JsObject(
      "accelerationX" -> JsNumber(acceleration.accelerationX),
      "accelerationY" -> JsNumber(acceleration.accelerationY)
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
    def write(collidable: CollidableComponent) =
      JsObject("collidable" -> JsBoolean(collidable.isCollidable()))
    def read(value: JsValue): CollidableComponent = {
      value.asJsObject.getFields("collidable") match {
        case Seq(JsBoolean(collidable)) => CollidableComponent.apply(collidable)
        case _ => throw DeserializationException("Collidable component expected")
      }
    }
  }

  implicit object VisibleFormatter extends RootJsonFormat[VisibleComponent] {
    def write(visible: VisibleComponent) = JsObject("visible" -> JsBoolean(visible.isVisible()))
    def read(value: JsValue): VisibleComponent = {
      value.asJsObject.getFields("visible") match {
        case Seq(JsBoolean(visible)) => VisibleComponent.apply(visible)
        case _ => throw DeserializationException("Collidable component expected")
      }
    }
  }

  implicit object DimensionFormatter extends RootJsonFormat[DimensionComponent] {
    def write(dimension: DimensionComponent) = JsObject("radius" -> JsNumber(dimension.radius))
    def read(value: JsValue): DimensionComponent = {
      value.asJsObject.getFields("radius") match {
        case Seq(JsNumber(radius)) => DimensionComponent.apply(radius.toDouble)
        case _ => throw DeserializationException("Dimension component expected")
      }
    }
  }

  implicit object PointFormatter extends RootJsonFormat[Point] {
    def write(point: Point) = JsObject("x" -> JsNumber(point.x), "y" -> JsNumber(point.y))
    def read(value: JsValue): Point = {
      value.asJsObject.getFields("x","y") match {
        case Seq(JsNumber(x), JsNumber(y)) => Point.apply(x.toDouble,y.toDouble)
        case _ => throw DeserializationException("Point expected")
      }
    }
  }

  implicit object PositionFormatter extends RootJsonFormat[PositionComponent] {
    def write(position: PositionComponent) = JsObject("point" -> position.point.toJson)
    def read(value: JsValue): PositionComponent = {
      value.asJsObject.getFields("point") match {
        case Seq(point) => PositionComponent.apply(point.convertTo[Point])
        case _ => throw DeserializationException("Position component expected")
      }
    }
  }

  implicit object SpeedFormatter extends RootJsonFormat[SpeedComponent] {
    def write(speed: SpeedComponent) =
      JsObject("speedX" -> JsNumber(speed.speedX), "speedY" -> JsNumber(speed.speedY))
    def read(value: JsValue): SpeedComponent = {
      value.asJsObject.getFields("speedX","speedY") match {
        case Seq(JsNumber(speedX), JsNumber(speedY)) =>
          SpeedComponent.apply(speedX.toDouble,speedY.toDouble)
        case _ => throw DeserializationException("Speed component expected")
      }
    }
  }

  implicit object EntityTypeFormatter extends RootJsonFormat[TypeComponent] {
    def write(entityType: TypeComponent) =
      JsObject("entity_type" ->  JsString(entityType.typeEntity.toString))
    def read(value: JsValue): TypeComponent = {
      value.asJsObject.getFields("entity_type") match {
        case Seq(JsString(entityType)) => TypeComponent.apply(EntityType.withName(entityType))
        case _ => throw DeserializationException("Type component component expected")
      }
    }
  }

  implicit object CellEntityFormatter extends RootJsonFormat[CellEntity] {
    def write(cellEntity: CellEntity) = JsObject(
      "acceleration" -> cellEntity.getAccelerationComponent.toJson,
      "collidable" -> cellEntity.getCollidableComponent.toJson,
      "dimension" -> cellEntity.getDimensionComponent.toJson,
      "position" -> cellEntity.getPositionComponent.toJson,
      "speed" -> cellEntity.getSpeedComponent.toJson,
      "visible" -> cellEntity.getVisibleComponent.toJson,
      "typeEntity" -> cellEntity.getTypeComponent.toJson)
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
    def write(playerCell: PlayerCellEntity) = JsObject(
      "acceleration" -> playerCell.getAccelerationComponent.toJson,
      "collidable" -> playerCell.getCollidableComponent.toJson,
      "dimension" -> playerCell.getDimensionComponent.toJson,
      "position" -> playerCell.getPositionComponent.toJson,
      "speed" -> playerCell.getSpeedComponent.toJson,
      "visible" -> playerCell.getVisibleComponent.toJson,
      "typeEntity" -> playerCell.getTypeComponent.toJson)
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
            typeEntity.convertTo[TypeComponent],
            SpawnerComponent(false))
        case _ => throw DeserializationException("Player cell entity expected")
      }
    }
  }

  implicit object MapShapeFormatter extends RootJsonFormat[MapShape] {
    def write(mapShape: MapShape): JsObject = mapShape match {
      case mapShape:MapShape.Rectangle => JsObject("mapShape" -> JsString(mapShape.mapShape),
        "height" -> JsNumber(mapShape.height),
        "base" -> JsNumber(mapShape.base))
      case mapShape:MapShape.Circle => JsObject("mapShape" -> JsString(mapShape.mapShape),
        "radius" -> JsNumber(mapShape.radius))
      case _ => throw new SerializationException("Shape " + mapShape.mapShape + " not managed!")
    }

    def read(value: JsValue): MapShape = {
      value.asJsObject.getFields("mapShape", "height", "base", "radius") match {
        case Seq(JsString(MapShape.rectangle), JsNumber(height), JsNumber(base)) =>
          MapShape.Rectangle(height.toDouble, base.toDouble)
        case Seq(JsString(MapShape.circle), JsNumber(radius)) => MapShape.Circle(radius.toDouble)
        case _ => throw DeserializationException("Map shape expected")
      }
    }
  }

  implicit object LevelMapFormatter extends RootJsonFormat[LevelMap] {
    def write(levelMap: LevelMap) = JsObject(
      "mapShape" -> levelMap.mapShape.toJson,
      "collisionRule" -> JsString(levelMap.collisionRule.toString))
    def read(value: JsValue): LevelMap = {
      value.asJsObject.getFields("mapShape","collisionRule") match {
        case Seq(mapShape, JsString(collisionRule)) =>
          LevelMap(mapShape.convertTo[MapShape], CollisionRules.withName(collisionRule))
        case _ => throw DeserializationException("Level map expected")
      }
    }
  }

  implicit object VictoryRuleFormatter extends RootJsonFormat[VictoryRules.Value] {
    def write(vicRule: VictoryRules.Value) = JsObject("victoryRule" -> JsString(vicRule.toString))
    def read(value: JsValue): VictoryRules.Value = {
      value.asJsObject.getFields("victoryRule") match {
        case Seq(JsString(victoryRule)) => VictoryRules.withName(victoryRule)
        case _ => throw DeserializationException("Victory rule expected expected")
      }
    }
  }

  implicit val levelFormatter:RootJsonFormat[Level] = jsonFormat5(Level)
}
