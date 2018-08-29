package it.unibo.osmos.redux

import it.unibo.osmos.redux.ecs.components._
import it.unibo.osmos.redux.ecs.entities.{CellEntity, EntityManager, EntityType, PlayerCellEntity}
import it.unibo.osmos.redux.ecs.systems.MovementSystem
import it.unibo.osmos.redux.utils.Point
import org.scalatest.{BeforeAndAfter, FunSuite}

class TestMovementSystem extends FunSuite with BeforeAndAfter {

  after {
    EntityManager.clear()
  }

<<<<<<< HEAD
  private def initEntityManager(mapShape: MapShape, collisionRules: CollisionRules.Value) {
    val levelInfo = Level("1",
      LevelMap(mapShape, collisionRules),
      null,
      VictoryRules.becomeTheBiggest,
      false)
    movementSystem = MovementSystem(levelInfo)
    EntityManager.subscribe(movementSystem, null)
  }

  test("Test speed and position update") {
    val mapShape = Rectangle((100, 150), 100, 150)
    initEntityManager(mapShape, CollisionRules.bouncing)
=======
  test("MovableProperty entities' acceleration, speed and position are updated correctly") {
    val movementSystem = MovementSystem()
>>>>>>> develop

    val ca = AccelerationComponent(1, 1)
    val cc = CollidableComponent(true)
    val cd = DimensionComponent(5)
    val cp = PositionComponent(Point(110, 170))
    val cs = SpeedComponent(4, 0)
    val cv = VisibleComponent(true)
    val ct = TypeComponent(EntityType.Matter)
    val cellEntity = CellEntity(ca, cc, cd, cp, cs, cv, ct)

    val pca = AccelerationComponent(-4, -1)
    val pcc = CollidableComponent(true)
    val pcd = DimensionComponent(5)
    val pcp = PositionComponent(Point(130, 150))
    val pcs = SpeedComponent(4, 0)
    val pcv = VisibleComponent(true)
    val pct = TypeComponent(EntityType.Matter)
    val spw = SpawnerComponent(false)
    val playerCellEntity = PlayerCellEntity(pca, pcc, pcd, pcp, pcs, pcv, pct, spw)

    EntityManager.add(cellEntity)
    EntityManager.add(playerCellEntity)

    movementSystem.update()

    assert(cellEntity.getSpeedComponent == SpeedComponent(5.0, 1.0))
    assert(cellEntity.getPositionComponent.point == Point(115.0, 171.0))
    assert(cellEntity.getAccelerationComponent == AccelerationComponent(0.0, 0.0))

    assert(playerCellEntity.getSpeedComponent == SpeedComponent(0.0, -1.0))
    assert(playerCellEntity.getPositionComponent.point == Point(130.0, 149.0))
    assert(playerCellEntity.getAccelerationComponent == AccelerationComponent(0.0, 0.0))
  }
}

/*
private def computePositionAfterBounce(currentPosition: Point, precPosition: Point, levelRadius: Double, levelCenter: Point): Point = {
val straightLine = GeometricalStraightLine(currentPosition, precPosition)
val circumference = GeometricalCircumference(levelCenter, levelRadius)

// computing intersection between straight line and circumference
val eq_a = 1 + Math.pow(straightLine.m, 2)
val eq_b = 2 * straightLine.m * straightLine.q + circumference.a + circumference.b * straightLine.m
val eq_c = Math.pow(straightLine.q, 2) + circumference.b * straightLine.q + circumference.c
val delta = Math.pow(eq_b, 2) - 4 * eq_a * eq_c

// x1,x2 = (-b ± sqrt(Δ) / 2 * a)
val x_1 = (-eq_b + Math.sqrt(delta)) / 2 * eq_a
val y_1 = straightLine.m * x_1 + straightLine.q
val p_1 = Point(x_1, y_1)

var tangentToCircumference: GeometricalStraightLine = null

MathUtils.isPointBetweenPoints(p_1, precPosition, currentPosition) match {
case true =>
val straightLineFromCenterToP_1 = GeometricalStraightLine(p_1, levelCenter)
val tang_m = -1 / straightLineFromCenterToP_1.m
val tang_q = straightLineFromCenterToP_1.q
tangentToCircumference = GeometricalStraightLine(tang_m, tang_q)
case false =>
// must compute second result
val x_2 = (-eq_b - Math.sqrt(delta)) / 2 * eq_a
val y_2 = straightLine.m * x_2 + straightLine.q
val p_2 = Point(x_2, y_2)

val straightLineFromCenterToP_2 = GeometricalStraightLine(p_2, levelCenter)
val tang_m = -1 / straightLineFromCenterToP_2.m
val tang_q = straightLineFromCenterToP_2.q
tangentToCircumference = GeometricalStraightLine(tang_m, tang_q)
}

val perpendicular_m = -1 / tangentToCircumference.m
val perpendicular_q = 1 / tangentToCircumference.m * currentPosition.x + currentPosition.y
val perpendicularOfTangent = GeometricalStraightLine(perpendicular_m, perpendicular_q)

val midPointX = (perpendicularOfTangent.q - tangentToCircumference.q) / (perpendicularOfTangent.m - tangentToCircumference.m)
val midPointY = tangentToCircumference.m * midPointX + tangentToCircumference.q

Point(2 * midPointX - currentPosition.x, 2 * midPointY - currentPosition.y)
}

trait GeometricalStraightLine {
  val m: Double
  val q: Double
}

object GeometricalStraightLine {
  def apply(p1: Point, p2: Point): GeometricalStraightLine = {
    // computes straight line between two points
    val m = (p2.y - p1.y) / (p2.x - p1.x)
    val q = p1.y - p1.x * (p2.y - p1.y) / (p2.x - p1.x)
    GeometricalStraightLineImpl(m, q)
  }

  def apply(m: Double, q: Double): GeometricalStraightLine = GeometricalStraightLineImpl(m, q)

  private case class GeometricalStraightLineImpl(override val m: Double, override val q: Double) extends GeometricalStraightLine {
    // val m: Double = (p2.y - p1.y) / (p2.x - p1.x)
    // val q: Double = p1.y - p1.x * (p2.y - p1.y) / (p2.x - p1.x)
  }

}

trait GeometricalCircumference {
  val a: Double
  val b: Double
  val c: Double
}

object GeometricalCircumference {
  def apply(center: Point, radius: Double): GeometricalCircumference = {
    // computes circumference given center and radius
    val a: Double = 2 * center.x
    val b: Double = -2 * center.y
    val c: Double = Math.pow(-a / 2, 2) + Math.pow(-b / 2, 2) - Math.pow(radius, 2)
    GeometricalCircumferenceImpl(a, b, c)
  }

  private case class GeometricalCircumferenceImpl(override val a: Double, override val b: Double, override val c: Double) extends GeometricalCircumference {
    //val a: Double = 2 * center.x
    //val b: Double = -2 * center.y
    //val c: Double = Math.pow(-a / 2, 2) + Math.pow(-b / 2, 2) - Math.pow(radius, 2)
  }

}
*/
