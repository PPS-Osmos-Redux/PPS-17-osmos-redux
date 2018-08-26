package it.unibo.osmos.redux

import it.unibo.osmos.redux.ecs.components._
import it.unibo.osmos.redux.ecs.entities.{CellEntity, EntityManager, PlayerCellEntity}
import it.unibo.osmos.redux.ecs.systems.MovementSystem
import it.unibo.osmos.redux.mvc.model.MapShape.{Circle, Rectangle}
import it.unibo.osmos.redux.mvc.model._
import it.unibo.osmos.redux.utils.{MathUtils, Point}
import org.scalatest.{BeforeAndAfter, FunSuite}

class TestMovementSystem extends FunSuite with BeforeAndAfter {

  var movementSystem: MovementSystem = _

  after {
    EntityManager.clear()
  }

  private def initEntityManager(mapShape: MapShape, collisionRules: CollisionRules.Value) {
    val levelInfo = Level(1,
      LevelMap(mapShape, collisionRules),
      null,
      VictoryRules.becomeTheBiggest,
      false)
    movementSystem = MovementSystem(levelInfo)
    EntityManager.subscribe(movementSystem, null)
  }

  test("Speed and position update") {
    val mapShape = Rectangle((100, 150), 100, 150)
    initEntityManager(mapShape, CollisionRules.bouncing)

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

  test("Rectangular shape field bouncing") {
    val mapShape = Rectangle((160, 100), 100, 160)
    initEntityManager(mapShape, CollisionRules.bouncing)

    val lcca = AccelerationComponent(0, 0)
    val lccc = CollidableComponent(true)
    val lccd = DimensionComponent(2)
    val lccp = PositionComponent(Point(83, 56))
    val lccs = SpeedComponent(-4, 2)
    val lccv = VisibleComponent(true)
    val lcct = TypeComponent(EntityType.Matter)
    val leftCollisionCellEntity = CellEntity(lcca, lccc, lccd, lccp, lccs, lccv, lcct)

    val rcca = AccelerationComponent(0, 0)
    val rccc = CollidableComponent(true)
    val rccd = DimensionComponent(7)
    val rccp = PositionComponent(Point(231, 90))
    val rccs = SpeedComponent(6, 0)
    val rccv = VisibleComponent(true)
    val rcct = TypeComponent(EntityType.Matter)
    val rightCollisionCellEntity = CellEntity(rcca, rccc, rccd, rccp, rccs, rccv, rcct)

    val tcca = AccelerationComponent(0, 0)
    val tccc = CollidableComponent(true)
    val tccd = DimensionComponent(8)
    val tccp = PositionComponent(Point(160, 60))
    val tccs = SpeedComponent(6, -4)
    val tccv = VisibleComponent(true)
    val tcct = TypeComponent(EntityType.Matter)
    val topCollisionCellEntity = CellEntity(tcca, tccc, tccd, tccp, tccs, tccv, tcct)

    val bcca = AccelerationComponent(0, 0)
    val bccc = CollidableComponent(true)
    val bccd = DimensionComponent(5)
    val bccp = PositionComponent(Point(115, 144))
    val bccs = SpeedComponent(-2, 7)
    val bccv = VisibleComponent(true)
    val bcct = TypeComponent(EntityType.Matter)
    val bottomCollisionCellEntity = CellEntity(bcca, bccc, bccd, bccp, bccs, bccv, bcct)

    EntityManager.add(leftCollisionCellEntity)
    EntityManager.add(rightCollisionCellEntity)
    EntityManager.add(topCollisionCellEntity)
    EntityManager.add(bottomCollisionCellEntity)

    movementSystem.update()

    assert(leftCollisionCellEntity.getSpeedComponent == SpeedComponent(4.0, 2.0))
    assert(leftCollisionCellEntity.getPositionComponent.point == Point(85.0, 58.0))

    assert(rightCollisionCellEntity.getSpeedComponent == SpeedComponent(-6.0, 0.0))
    assert(rightCollisionCellEntity.getPositionComponent.point == Point(229.0, 90.0))

    assert(topCollisionCellEntity.getSpeedComponent == SpeedComponent(6.0, 4.0))
    assert(topCollisionCellEntity.getPositionComponent.point == Point(166.0, 60.0))

    assert(bottomCollisionCellEntity.getSpeedComponent == SpeedComponent(-2.0, -7.0))
    assert(bottomCollisionCellEntity.getPositionComponent.point == Point(113.0, 139.0))
  }

  test("Rectangular shape field instant death rule") {
    val mapShape = Rectangle((160, 100), 100, 160)
    initEntityManager(mapShape, CollisionRules.instantDeath)

    val lcca = AccelerationComponent(0, 0)
    val lccc = CollidableComponent(true)
    val lccd = DimensionComponent(2)
    val lccp = PositionComponent(Point(83, 56))
    val lccs = SpeedComponent(-4, 2)
    val lccv = VisibleComponent(true)
    val lcct = TypeComponent(EntityType.Matter)
    val leftCollisionCellEntity = CellEntity(lcca, lccc, lccd, lccp, lccs, lccv, lcct)

    val rcca = AccelerationComponent(0, 0)
    val rccc = CollidableComponent(true)
    val rccd = DimensionComponent(7)
    val rccp = PositionComponent(Point(231, 90))
    val rccs = SpeedComponent(6, 0)
    val rccv = VisibleComponent(true)
    val rcct = TypeComponent(EntityType.Matter)
    val rightCollisionCellEntity = CellEntity(rcca, rccc, rccd, rccp, rccs, rccv, rcct)

    val tcca = AccelerationComponent(0, 0)
    val tccc = CollidableComponent(true)
    val tccd = DimensionComponent(8)
    val tccp = PositionComponent(Point(160, 60))
    val tccs = SpeedComponent(6, -4)
    val tccv = VisibleComponent(true)
    val tcct = TypeComponent(EntityType.Matter)
    val topCollisionCellEntity = CellEntity(tcca, tccc, tccd, tccp, tccs, tccv, tcct)

    val bcca = AccelerationComponent(0, 0)
    val bccc = CollidableComponent(true)
    val bccd = DimensionComponent(5)
    val bccp = PositionComponent(Point(115, 144))
    val bccs = SpeedComponent(-2, 7)
    val bccv = VisibleComponent(true)
    val bcct = TypeComponent(EntityType.Matter)
    val bottomCollisionCellEntity = CellEntity(bcca, bccc, bccd, bccp, bccs, bccv, bcct)

    EntityManager.add(leftCollisionCellEntity)
    EntityManager.add(rightCollisionCellEntity)
    EntityManager.add(topCollisionCellEntity)
    EntityManager.add(bottomCollisionCellEntity)

    movementSystem.update()

    assert(leftCollisionCellEntity.getSpeedComponent == lccs)
    assert(leftCollisionCellEntity.getPositionComponent.point == Point(79.0, 58.0))
    assert(leftCollisionCellEntity.getDimensionComponent.radius == -1)

    assert(rightCollisionCellEntity.getSpeedComponent == rccs)
    assert(rightCollisionCellEntity.getPositionComponent.point == Point(237.0, 90.0))
    assert(rightCollisionCellEntity.getDimensionComponent.radius == 3.0)

    assert(topCollisionCellEntity.getSpeedComponent == tccs)
    assert(topCollisionCellEntity.getPositionComponent.point == Point(166.0, 56.0))
    assert(topCollisionCellEntity.getDimensionComponent.radius == 6.0)

    assert(bottomCollisionCellEntity.getSpeedComponent == bccs)
    assert(bottomCollisionCellEntity.getPositionComponent.point == Point(113.0, 151.0))
    assert(bottomCollisionCellEntity.getDimensionComponent.radius == -1)
  }

  test("Circular shape field bouncing") {
    val levelCenter = Point(300.0, 300.0)
    val levelRadius = 200.0
    val mapShape = Circle((levelCenter.x, levelCenter.y), levelRadius)
    initEntityManager(mapShape, CollisionRules.bouncing)

    val ca = AccelerationComponent(0, 0)
    val cc = CollidableComponent(true)
    val cd = DimensionComponent(20)
    val cp = PositionComponent(Point(118, 300))
    val cs = SpeedComponent(-10.0, -20.0)
    val cv = VisibleComponent(true)
    val ct = TypeComponent(EntityType.Matter)
    val cellEntity = CellEntity(ca, cc, cd, cp, cs, cv, ct)

    EntityManager.add(cellEntity)

    movementSystem.update()

    assert(cellEntity.getPositionComponent.point == Point(120.04654311426577, 304.09308622853155))
    assert(cellEntity.getSpeedComponent.vector == utils.Vector(9.080318896799085, -20.43398660889337))
  }

  test("Circular shape field instant death") {
    val levelCenter = Point(300.0, 300.0)
    val levelRadius = 200.0
    val mapShape = Circle((levelCenter.x, levelCenter.y), levelRadius)
    initEntityManager(mapShape, CollisionRules.instantDeath)

    val ca = AccelerationComponent(0, 0)
    val cc = CollidableComponent(true)
    val cd = DimensionComponent(20)
    val cp = PositionComponent(Point(118, 300))
    val cs = SpeedComponent(-10.0, -20.0)
    val cv = VisibleComponent(true)
    val ct = TypeComponent(EntityType.Matter)
    val cellEntity = CellEntity(ca, cc, cd, cp, cs, cv, ct)

    EntityManager.add(cellEntity)

    movementSystem.update()

    assert(cellEntity.getPositionComponent.point == Point(108.0, 280.0))
    assert(cellEntity.getSpeedComponent.vector == utils.Vector(-10.0, -20.0))
    assert(cellEntity.getDimensionComponent.radius == 6.961143807781525)
  }

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
