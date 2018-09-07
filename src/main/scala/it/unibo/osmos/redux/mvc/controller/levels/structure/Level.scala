package it.unibo.osmos.redux.mvc.controller.levels.structure

import it.unibo.osmos.redux.ecs.entities.CellEntity
import it.unibo.osmos.redux.mvc.controller.levels.structure.MapShape.{Circle, Rectangle}
import it.unibo.osmos.redux.utils.{Logger, MathUtils, Point}

/**List of cell types.*/
object CellType {
  val sentientCell = "sentientCell"
  val gravityCell = "gravityCell"
  val playerCell = "playerCell"
  val basicCell = "basicCell"
}

/** Level configuratoin
  *
  * @param levelInfo LevelInfo
  * @param levelMap LevelMap
  * @param entities List[CellEntity]
  */
case class Level(var levelInfo:LevelInfo,
                 levelMap:LevelMap,
                 var entities:List[CellEntity]) {

  implicit val who:String = "Level"

  /**Check if the cells are into the map boundaries*/
  def checkCellPosition():Unit = levelMap.mapShape match {
    case rectangle:Rectangle => rectangularMapCheck(rectangle)
    case circle:Circle => circularMapCheck(circle)
    case _ => Logger.log("Map shape not managed [checkCellPosition]")
  }

  private def rectangularMapCheck(rectangle:Rectangle): Unit = {
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

  private def circularMapCheck(circle:Circle): Unit =
    entities = entities.map(ent => (ent, MathUtils.euclideanDistance(ent.getPositionComponent.point,
                                                                      Point(circle.center.x, circle.center.y)) +
                                                                      ent.getDimensionComponent.radius))
                       .filterNot(tup => tup._2 > circle.radius)
                       .map(t => t._1)
}
