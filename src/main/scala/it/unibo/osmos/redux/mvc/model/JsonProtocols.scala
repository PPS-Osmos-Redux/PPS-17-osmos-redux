package it.unibo.osmos.redux.mvc.model
import it.unibo.osmos.redux.ecs.components._
import it.unibo.osmos.redux.ecs.entities.{CellEntity, GravityCellEntity, PlayerCellEntity, SentientCellEntity, _}
import it.unibo.osmos.redux.mvc.controller.LevelInfo.LevelInfo
import it.unibo.osmos.redux.mvc.view.drawables.DrawableWrapper
import it.unibo.osmos.redux.utils.Point
import org.apache.commons.lang3.SerializationException
import spray.json.DefaultJsonProtocol._
import spray.json._

/**
  * Json implicit stategies for: convert json to Level or convert Level to json
  */
object JsonProtocols {
  implicit object AccelerationFormatter extends RootJsonFormat[AccelerationComponent] {
    def write(acceleration: AccelerationComponent) = JsObject(
      "accelerationX" -> JsNumber(acceleration.vector.x),
      "accelerationY" -> JsNumber(acceleration.vector.y)
    )
    def read(value: JsValue): AccelerationComponent = {
      value.asJsObject.getFields("accelerationX", "accelerationY") match {
        case Seq(JsNumber(accelerationX), JsNumber(accelerationY)) =>
          AccelerationComponent(accelerationX.toDouble,accelerationY.toDouble)
        case _ => throw DeserializationException("Acceleration component expected")
      }
    }
  }

  implicit object CollidableFormatter extends RootJsonFormat[CollidableComponent] {
    def write(collidable: CollidableComponent) =
      JsObject("collidable" -> JsBoolean(collidable.isCollidable))
    def read(value: JsValue): CollidableComponent = {
      value.asJsObject.getFields("collidable") match {
        case Seq(JsBoolean(collidable)) => CollidableComponent(collidable)
        case _ => throw DeserializationException("Collidable component expected")
      }
    }
  }

  implicit object VisibleFormatter extends RootJsonFormat[VisibleComponent] {
    def write(visible: VisibleComponent) = JsObject("visible" -> JsBoolean(visible.isVisible))
    def read(value: JsValue): VisibleComponent = {
      value.asJsObject.getFields("visible") match {
        case Seq(JsBoolean(visible)) => VisibleComponent(visible)
        case _ => throw DeserializationException("Collidable component expected")
      }
    }
  }

  implicit object DimensionFormatter extends RootJsonFormat[DimensionComponent] {
    def write(dimension: DimensionComponent) = JsObject("radius" -> JsNumber(dimension.radius))
    def read(value: JsValue): DimensionComponent = {
      value.asJsObject.getFields("radius") match {
        case Seq(JsNumber(radius)) => DimensionComponent(radius.toDouble)
        case _ => throw DeserializationException("Dimension component expected")
      }
    }
  }

  implicit object PointFormatter extends RootJsonFormat[Point] {
    def write(point: Point) = JsObject("x" -> JsNumber(point.x), "y" -> JsNumber(point.y))
    def read(value: JsValue): Point = {
      value.asJsObject.getFields("x","y") match {
        case Seq(JsNumber(x), JsNumber(y)) => Point(x.toDouble,y.toDouble)
        case _ => throw DeserializationException("Point expected")
      }
    }
  }

  implicit object PositionFormatter extends RootJsonFormat[PositionComponent] {
    def write(position: PositionComponent) = JsObject("point" -> position.point.toJson)
    def read(value: JsValue): PositionComponent = {
      value.asJsObject.getFields("point") match {
        case Seq(point) => PositionComponent(point.convertTo[Point])
        case _ => throw DeserializationException("Position component expected")
      }
    }
  }

  implicit object SpeedFormatter extends RootJsonFormat[SpeedComponent] {
    def write(speed: SpeedComponent) =
      JsObject("speedX" -> JsNumber(speed.vector.x), "speedY" -> JsNumber(speed.vector.y))
    def read(value: JsValue): SpeedComponent = {
      value.asJsObject.getFields("speedX","speedY") match {
        case Seq(JsNumber(speedX), JsNumber(speedY)) =>
          SpeedComponent(speedX.toDouble,speedY.toDouble)
        case _ => throw DeserializationException("Speed component expected")
      }
    }
  }

  implicit object EntityTypeFormatter extends RootJsonFormat[EntityType.Value] {
    def write(entityType: EntityType.Value) =
      JsObject("entityType" -> JsString(entityType.toString))
    def read(value: JsValue): EntityType.Value = {
      value.asJsObject.getFields("entityType") match {
        case Seq(JsString(entityType)) => EntityType.withName(entityType)
        case _ => throw DeserializationException("EntityType component expected")
      }
    }
  }

  implicit object ComponentTypeFormatter extends RootJsonFormat[TypeComponent] {
    def write(entityType: TypeComponent) =
      JsObject("componentType" ->  JsString(entityType.typeEntity.toString))
    def read(value: JsValue): TypeComponent = {
      value.asJsObject.getFields("componentType") match {
        case Seq(JsString(componentType)) => TypeComponent(EntityType.withName(componentType))
        case _ => throw DeserializationException("Type component component expected")
      }
    }
  }

  implicit object SpawnerFormatter extends RootJsonFormat[SpawnerComponent] {
    def write(spawner: SpawnerComponent) = JsObject("canSpawn" -> JsBoolean(spawner.canSpawn))
    def read(value: JsValue): SpawnerComponent = {
      value.asJsObject.getFields("canSpawn") match {
        case Seq(JsBoolean(canSpawn)) => SpawnerComponent(canSpawn)
        case _ => throw DeserializationException("Spawner expected")
      }
    }
  }

  implicit object SpecificWeightFormatter extends RootJsonFormat[SpecificWeightComponent] {
    def write(specificWeight: SpecificWeightComponent) = JsObject("specificWeight" -> JsNumber(specificWeight.specificWeight))
    def read(value: JsValue): SpecificWeightComponent = {
      value.asJsObject.getFields("specificWeight") match {
        case Seq(JsNumber(specificWeight)) => SpecificWeightComponent(specificWeight.toDouble)
        case _ => throw DeserializationException("Specific weight component expected")
      }
    }
  }

  implicit object GravityCellEntityFormatter extends RootJsonFormat[GravityCellEntity] {
    def write(gravityCell: GravityCellEntity) = JsObject(
      "cellType" -> JsString(CellType.gravityCell),
      "acceleration" -> gravityCell.getAccelerationComponent.toJson,
      "collidable" -> gravityCell.getCollidableComponent.toJson,
      "dimension" -> gravityCell.getDimensionComponent.toJson,
      "position" -> gravityCell.getPositionComponent.toJson,
      "speed" -> gravityCell.getSpeedComponent.toJson,
      "visible" -> gravityCell.getVisibleComponent.toJson,
      "typeEntity" -> gravityCell.getTypeComponent.toJson,
      "specificWeight" -> gravityCell.getSpecificWeightComponent.toJson)
    def read(value: JsValue): GravityCellEntity = {
      value.asJsObject.getFields("acceleration",
        "collidable",
        "dimension",
        "position",
        "speed",
        "visible",
        "typeEntity",
        "specificWeight") match {
        case Seq(acceleration, collidable, dimension, position, speed, visible, typeEntity, specificWeight) =>
          GravityCellEntity(acceleration.convertTo[AccelerationComponent],
            collidable.convertTo[CollidableComponent],
            dimension.convertTo[DimensionComponent],
            position.convertTo[PositionComponent],
            speed.convertTo[SpeedComponent],
            visible.convertTo[VisibleComponent],
            typeEntity.convertTo[TypeComponent],
            specificWeight.convertTo[SpecificWeightComponent])
        case _ => throw DeserializationException("Gravity cell entity expected")
      }
    }
  }

  implicit object PlayerCellEntityFormatter extends RootJsonFormat[PlayerCellEntity] {
    def write(playerCell: PlayerCellEntity) = JsObject(
      "cellType" -> JsString(CellType.playerCell),
      "acceleration" -> playerCell.getAccelerationComponent.toJson,
      "collidable" -> playerCell.getCollidableComponent.toJson,
      "dimension" -> playerCell.getDimensionComponent.toJson,
      "position" -> playerCell.getPositionComponent.toJson,
      "speed" -> playerCell.getSpeedComponent.toJson,
      "visible" -> playerCell.getVisibleComponent.toJson,
      "typeEntity" -> playerCell.getTypeComponent.toJson,
      "spawner" -> playerCell.getSpawnerComponent.toJson)
    def read(value: JsValue): PlayerCellEntity = {
      value.asJsObject.getFields("acceleration",
        "collidable",
        "dimension",
        "position",
        "speed",
        "visible",
        "typeEntity",
        "spawner") match {
        case Seq(acceleration, collidable, dimension, position, speed, visible, typeEntity, spawner) =>
          PlayerCellEntity(acceleration.convertTo[AccelerationComponent],
            collidable.convertTo[CollidableComponent],
            dimension.convertTo[DimensionComponent],
            position.convertTo[PositionComponent],
            speed.convertTo[SpeedComponent],
            visible.convertTo[VisibleComponent],
            typeEntity.convertTo[TypeComponent],
            spawner.convertTo[SpawnerComponent])
        case _ => throw DeserializationException("Player cell entity expected")
      }
    }
  }

  implicit object SentientCellEntityFormatter extends RootJsonFormat[SentientCellEntity] {
    def write(sentientCell: SentientCellEntity) = JsObject(
      "cellType" -> JsString(CellType.sentientCell),
      "acceleration" -> sentientCell.getAccelerationComponent.toJson,
      "collidable" -> sentientCell.getCollidableComponent.toJson,
      "dimension" -> sentientCell.getDimensionComponent.toJson,
      "position" -> sentientCell.getPositionComponent.toJson,
      "speed" -> sentientCell.getSpeedComponent.toJson,
      "visible" -> sentientCell.getVisibleComponent.toJson,
      "spawner" -> sentientCell.getSpawnerComponent.toJson)
    def read(value: JsValue): SentientCellEntity = {
      value.asJsObject.getFields("acceleration",
        "collidable",
        "dimension",
        "position",
        "speed",
        "visible",
        "spawner") match {
        case Seq(acceleration, collidable, dimension, position, speed, visible, spawner) =>
          SentientCellEntity(acceleration.convertTo[AccelerationComponent],
            collidable.convertTo[CollidableComponent],
            dimension.convertTo[DimensionComponent],
            position.convertTo[PositionComponent],
            speed.convertTo[SpeedComponent],
            visible.convertTo[VisibleComponent],
            spawner.convertTo[SpawnerComponent])
        case _ => throw DeserializationException("Sentient cell entity expected")
      }
    }
  }

  implicit object CellEntityFormatter extends RootJsonFormat[CellEntity] {
    def write(cellEntity: CellEntity): JsValue = cellEntity match {
      case sc : SentientCellEntity => sc.toJson
      case gc : GravityCellEntity => gc.toJson
      case pce : PlayerCellEntity => pce.toJson
      case _ : CellEntity => JsObject( "cellType" -> JsString(CellType.basicCell),
        "acceleration" -> cellEntity.getAccelerationComponent.toJson,
        "collidable" -> cellEntity.getCollidableComponent.toJson,
        "dimension" -> cellEntity.getDimensionComponent.toJson,
        "position" -> cellEntity.getPositionComponent.toJson,
        "speed" -> cellEntity.getSpeedComponent.toJson,
        "visible" -> cellEntity.getVisibleComponent.toJson,
        "typeEntity" -> cellEntity.getTypeComponent.toJson)
      case _ => println("Cell ", cellEntity, " currently is not managed!"); JsObject()
    }

    def read(value: JsValue): CellEntity = {
      value.asJsObject.getFields("cellType") match {
        case Seq(JsString(CellType.basicCell)) =>
          value.asJsObject.getFields("acceleration",
            "collidable",
            "dimension",
            "position",
            "speed",
            "visible",
            "typeEntity") match {
            case Seq(acceleration, collidable, dimension, position, speed, visible, typeEntity) =>
              CellEntity(acceleration.convertTo[AccelerationComponent],
                collidable.convertTo[CollidableComponent],
                dimension.convertTo[DimensionComponent],
                position.convertTo[PositionComponent],
                speed.convertTo[SpeedComponent],
                visible.convertTo[VisibleComponent],
                typeEntity.convertTo[TypeComponent])
            case _ => throw DeserializationException("Cell entity expected")
          }
        case Seq(JsString(CellType.sentientCell)) =>
          value.convertTo[SentientCellEntity]
        case Seq(JsString(CellType.gravityCell)) =>
          value.convertTo[GravityCellEntity]
        case Seq(JsString(CellType.playerCell)) =>
          value.convertTo[PlayerCellEntity]
        case _ => throw DeserializationException("Cell entity expected")
      }
    }
  }

  implicit object MapShapeFormatter extends RootJsonFormat[MapShape] {
    def write(mapShape: MapShape): JsObject = mapShape match {
      case mapShape: MapShape.Rectangle => JsObject("centerX" -> JsNumber(mapShape.center._1),
        "centerY" -> JsNumber(mapShape.center._2),
        "mapShape" -> JsString(mapShape.mapShape.toString),
        "height" -> JsNumber(mapShape.height),
        "base" -> JsNumber(mapShape.base))
      case mapShape: MapShape.Circle => JsObject("centerX" -> JsNumber(mapShape.center._1),
        "centerY" -> JsNumber(mapShape.center._2),
        "mapShape" -> JsString(mapShape.mapShape.toString),
        "radius" -> JsNumber(mapShape.radius))
      case _ => throw new SerializationException("Shape " + mapShape.mapShape + " not managed!")
    }

    def read(value: JsValue): MapShape = {
      val rectangle  = MapShapeType.Rectangle.toString
      val circle  = MapShapeType.Circle.toString

      value.asJsObject.getFields("centerX", "centerY", "mapShape") match {
        case Seq(JsNumber(centerX), JsNumber(centerY), JsString(`rectangle`)) =>
          value.asJsObject.getFields("height", "base") match {
            case Seq(JsNumber(height), JsNumber(base)) =>
              MapShape.Rectangle((centerX.toDouble, centerY.toDouble), height.toDouble, base.toDouble)
            case _ => throw DeserializationException("Rectangular map expected")
          }
        case Seq(JsNumber(centerX), JsNumber(centerY), JsString(`circle`)) =>
          value.asJsObject.getFields("radius") match {
            case Seq(JsNumber(radius)) =>
              MapShape.Circle((centerX.toDouble, centerY.toDouble), radius.toDouble)
            case _ => throw DeserializationException("Circular map expected")
          }
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

  /**
    * Take from levels only name and victoryRule
    */
  implicit object LevelInfoFormatter extends RootJsonFormat[LevelInfo] {
    def write(levelInfo: LevelInfo) = JsObject(
      "levelId" -> JsString(levelInfo.name),
      "victoryRule" -> levelInfo.victoryRule.toJson)
    def read(value: JsValue): LevelInfo = {
      value.asJsObject.getFields("levelId","victoryRule") match {
        case Seq(JsString(name), victoryRule) =>
          LevelInfo(name, victoryRule.convertTo[VictoryRules.Value])
        case _ => throw DeserializationException("Level info expected")
      }
    }
  }

  implicit val levelFormatter:RootJsonFormat[Level] = jsonFormat5(Level)

  implicit val drawableWrapperFormatter:RootJsonFormat[DrawableWrapper] = jsonFormat4(DrawableWrapper)

  implicit  val levelStatFormatter:RootJsonFormat[LevelStat] = jsonFormat3(LevelStat)

  implicit val userProgressFormatter:RootJsonFormat[UserStat] = jsonFormat2(UserStat)
}
