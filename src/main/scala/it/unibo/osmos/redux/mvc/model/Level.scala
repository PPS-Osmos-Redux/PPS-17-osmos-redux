package it.unibo.osmos.redux.mvc.model

import it.unibo.osmos.redux.ecs.entities.CellEntity
import it.unibo.osmos.redux.mvc.controller.LevelInfo
import it.unibo.osmos.redux.mvc.model.MapShape.{Circle, Rectangle}
import it.unibo.osmos.redux.utils.{Logger, MathUtils, Point}

/**
  * List of cell types
  */
object CellType {
  val sentientCell = "sentientCell"
  val gravityCell = "gravityCell"
  val playerCell = "playerCell"
  val basicCell = "basicCell"
}

/**
  * Map edges collision rules
  */
object CollisionRules extends Enumeration {
  val bouncing: CollisionRules.Value = Value("Bouncing")
  val instantDeath: CollisionRules.Value = Value("Instant_death")
}

/**
  * Level victory rules
  */
object VictoryRules extends Enumeration {
  val becomeTheBiggest: VictoryRules.Value = Value("Become_the_biggest")
  val becomeHuge: VictoryRules.Value = Value("Become_huge")
  val absorbTheRepulsors: VictoryRules.Value = Value("Absorb_the_repulsors")
  val absorbTheHostileCells: VictoryRules.Value = Value("Absorb_the_hostile_cells")
  val absorbAllOtherPlayers: VictoryRules.Value = Value("Absorb_all_other_players")
}

/**
  * Map shape data structure
  */
object MapShapeType extends Enumeration {
  val Circle, Rectangle= Value
}
sealed trait MapShape {
  val mapShape:MapShapeType.Value
  val center:Point
}

object MapShape {
  /**
    * Rectangular level map
    * @param center center of map
    * @param height rectangle height
    * @param base rectangle base
    */
  case class Rectangle(override val center: Point, height:Double, base:Double)
                                                                          extends MapShape {
    override val mapShape: MapShapeType.Value = MapShapeType.Rectangle
  }

  /**
    * Circular level map
    * @param center center of map
    * @param radius circle radius
    */
  case class Circle(override val center: Point, radius:Double) extends MapShape {
    override val mapShape: MapShapeType.Value = MapShapeType.Circle
  }
}

/**
  * Map of a level
  * @param mapShape map shape
  * @param collisionRule edges collision rule
  */
case class LevelMap(mapShape:MapShape, collisionRule:CollisionRules.Value)

/**
  * Level configuratoin
  * @param levelInfo LevelInfo
  * @param levelMap level map
  * @param entities list of level entities
  */
case class Level(var levelInfo:LevelInfo,
                 levelMap:LevelMap,
                 var entities:List[CellEntity]) {

  def checkCellPosition():Unit = levelMap.mapShape match {
    case rectangle:Rectangle => rectangularMapCheck(rectangle)
    case circle:Circle => circularMapCheck(circle)
    case _ => Logger.log("Map shape not managed [checkCellPosition]")("Level")
  }

  /**
    * Remove all entities who aren't into map boundary
    * @param rectangle map shape
    */
  def rectangularMapCheck(rectangle:Rectangle): Unit = {
    /*calculate map bound*/
    var westMiddlePointX = rectangle.center.x - (rectangle.base/2)
    var northMiddlePointY = rectangle.center.y - (rectangle.height/2)
    //const for translate point if they are negative
    val kx:Double = if(westMiddlePointX < 0) -westMiddlePointX else 0
    val ky:Double = if(northMiddlePointY < 0) -northMiddlePointY else 0

    westMiddlePointX = westMiddlePointX + kx
    northMiddlePointY = northMiddlePointY + ky
    val southMiddlePointY = northMiddlePointY + rectangle.height
    val eastMiddlePointX = westMiddlePointX + rectangle.base
    entities = entities.filterNot(ent => {
      //calculate cell point
      val cellCenter = Point(ent.getPositionComponent.point.x + kx, ent.getPositionComponent.point.y + ky)
      val topY = cellCenter.y - ent.getDimensionComponent.radius
      val rightX = cellCenter.x + ent.getDimensionComponent.radius
      val bottomY = topY + 2*ent.getDimensionComponent.radius
      val leftX = rightX - 2*ent.getDimensionComponent.radius
      //check if cell is into map
      !(leftX >= westMiddlePointX && rightX <= eastMiddlePointX) || !(topY >= northMiddlePointY && bottomY <= southMiddlePointY)
    })
  }

  def circularMapCheck(circle:Circle): Unit =
    entities = entities.map(ent => (ent, MathUtils.euclideanDistance(ent.getPositionComponent.point,
                                                                      Point(circle.center.x, circle.center.y)) +
                                                                      ent.getDimensionComponent.radius))
                       .filterNot(tup => tup._2 > circle.radius)
                       .map(t => t._1)
}
