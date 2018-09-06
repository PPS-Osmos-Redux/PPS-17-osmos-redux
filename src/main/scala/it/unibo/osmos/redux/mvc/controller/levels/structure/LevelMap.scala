package it.unibo.osmos.redux.mvc.controller.levels.structure

import it.unibo.osmos.redux.utils.Point

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
