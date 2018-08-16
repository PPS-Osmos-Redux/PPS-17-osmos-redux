package it.unibo.osmos.redux.mvc.model
import it.unibo.osmos.redux.ecs.entities.CellEntity

/**
  * List of cell types
  */
object CellType {
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
}

/**
  * Map shape data structure
  */
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

/**
  * Map of a level
  * @param mapShape map shape
  * @param collisionRule edges collision rule
  */
case class LevelMap(mapShape:MapShape, collisionRule:CollisionRules.Value)

/**
  * Level configuratoin
  * @param levelId level identifier
  * @param levelMap level map
  * @param entities list of level entities
  * @param victoryRule victory rule
  */
case class Level(levelId:Int,
                 levelMap:LevelMap,
                 entities:List[CellEntity],
                 victoryRule:VictoryRules.Value)
