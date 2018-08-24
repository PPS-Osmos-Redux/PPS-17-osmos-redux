package it.unibo.osmos.redux.mvc.model
import it.unibo.osmos.redux.ecs.components._
import it.unibo.osmos.redux.ecs.entities.{CellEntity, GravityCellEntity, PlayerCellEntity, SentientCellEntity}
import it.unibo.osmos.redux.mvc.model.MapShape.{Circle, Rectangle}
import it.unibo.osmos.redux.utils.Point

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
}

/**
  * Map shape data structure
  */
sealed trait MapShape {
  val mapShape:String
  val center:(Double,Double)
}
object MapShape {
  val rectangle:String = "RECTANGLE"
  val circle:String = "CIRCLE"

  /**
    * Rectangular level map
    * @param center center of map
    * @param height rectangle height
    * @param base rectangle base
    */
  case class Rectangle(override val center: (Double, Double), height:Double, base:Double)
                                                                          extends MapShape {
    override val mapShape: String = MapShape.rectangle
  }

  /**
    * Circular level map
    * @param center center of map
    * @param radius circle radius
    */
  case class Circle(override val center: (Double, Double), radius:Double) extends MapShape {
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
  * @param isSimulation if it's a simulation
  */
case class Level(levelId:Int,
                 levelMap:LevelMap,
                 var entities:List[CellEntity],
                 victoryRule:VictoryRules.Value, var isSimulation:Boolean = false) {

  def checkCellPosition():Unit = levelMap.mapShape match {
    case rec:Rectangle => rectangularMapCheck(rec)
    case circ:Circle => circularMapCheck(circ)
  }

  /**
    * Remove all entities who aren't into map boundary
    * @param rectangle map shape
    */
  def rectangularMapCheck(rectangle:Rectangle): Unit = {
    /*calculate map bound*/
    var westMiddlePointX = rectangle.center._1 - (rectangle.base/2)
    var northMiddlePointY = rectangle.center._2 - (rectangle.height/2)
    //const for translate point if they are negative
    val kx:Double = if(westMiddlePointX < 0) -westMiddlePointX else 0
    val ky:Double = if(northMiddlePointY < 0) -northMiddlePointY else 0

    westMiddlePointX = westMiddlePointX + kx
    northMiddlePointY = northMiddlePointY + ky
    val southMiddlePointY = northMiddlePointY + rectangle.height
    val eastMiddlePointX = westMiddlePointX + rectangle.base

    entities.foreach(ent => {
      //calculate cell point
      val cellCenter = Point(ent.getPositionComponent.point.x + kx, ent.getPositionComponent.point.y + ky)
      val topY = cellCenter.y - ent.getDimensionComponent.radius
      val rightX = cellCenter.x + ent.getDimensionComponent.radius
      val bottomY = topY + 2*ent.getDimensionComponent.radius
      val leftX = rightX - 2*ent.getDimensionComponent.radius
      //check if cell is into map
      if(!(leftX >= westMiddlePointX && rightX <= eastMiddlePointX) ||
         !(topY >= northMiddlePointY && bottomY <= southMiddlePointY)) {
        entities = entities.filterNot(entity => entity.equals(ent))
      }
    })
  }

  def circularMapCheck(circle:Circle): Unit = {
    entities.foreach(entity => {
      val d = Math.sqrt(Math.pow(circle.center._1 - entity.getPositionComponent.point.x, 2) + Math.pow(circle.center._2 - entity.getPositionComponent.point.y, 2))
      val k = d+entity.getDimensionComponent.radius
      println("--k: ", k)
      println("k > c.r: ", k > circle.radius)
      if(k > circle.radius) {
        entities = entities.filterNot(entity2 => entity2.equals(entity))
      }
    })
  }
}

object app extends App {

  val level = getLevel
  level.checkCellPosition()
  println(level.entities.map(ent => ent.getClass.getName))





















  def getLevel:Level = {
    //Components
    val a = AccelerationComponent(1, 1)
    val c = CollidableComponent(true)


    val d = DimensionComponent(1)

    val p = PositionComponent(Point(0, 0))
    val p1 = PositionComponent(Point(-2, -2))
    val p2= PositionComponent(Point(4,1))
    val p3 = PositionComponent(Point(-2.1, 2.1))

    val s = SpeedComponent(4, 0)
    val v = VisibleComponent(true)
    val et = TypeComponent(EntityType.Matter)
    val sp = SpawnerComponent(true)
    val sw = SpecificWeightComponent(1)
    //Entities
    val ce = CellEntity(a, c, d, p, s, v, et)
    val pce = PlayerCellEntity(a, c, d, p1, s, v, et, sp)
    val gc = GravityCellEntity(a, c, d, p2, s, v, et, sw)
    val sc = SentientCellEntity(a, c, d, p3, s, v)
    val listCell:List[CellEntity] = List(ce, pce, gc, sc)
    //LevelMap
    val rectangle:MapShape = Rectangle((0,0),10,10)
    val circle:MapShape = Circle((0,0), 4)
    val listShape:List[MapShape] = List(rectangle, circle)
    val levelMap:LevelMap = LevelMap(circle, CollisionRules.bouncing)
    //Level
    val level:Level = Level(levelId = 1,
      levelMap,
      listCell,
      VictoryRules.becomeTheBiggest)
    level
  }
}
