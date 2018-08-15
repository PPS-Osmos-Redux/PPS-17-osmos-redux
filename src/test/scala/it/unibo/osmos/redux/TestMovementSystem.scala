package it.unibo.osmos.redux

import it.unibo.osmos.redux.ecs.components._
import it.unibo.osmos.redux.ecs.entities.{CellEntity, EntityManager, PlayerCellEntity}
import it.unibo.osmos.redux.ecs.systems.MovementSystem
import it.unibo.osmos.redux.utils.Point
import org.scalamock.matchers.Matchers
import org.scalamock.scalatest.MockFactory
import org.scalatest.FunSuite

class TestMovementSystem extends FunSuite with MockFactory with Matchers {

  test(" Test movement system behaviour ") {

    val movementSystem = MovementSystem(0)
    EntityManager.subscribe(movementSystem, null)

    val ca = AccelerationComponent(1, 1)
    val cc = CollidableComponent(true)
    val cd = DimensionComponent(5)
    val cp = PositionComponent(Point(0, 0))
    val cs = SpeedComponent(4, 0)
    val cv = VisibleComponent(true)
    val ct = TypeComponent(EntityType.Material)
    val cellEntity = CellEntity(ca, cc, cd, cp, cs, cv, ct)

    val pca = AccelerationComponent(-4, -1)
    val pcc = CollidableComponent(true)
    val pcd = DimensionComponent(5)
    val pcp = PositionComponent(Point(-4, 6))
    val pcs = SpeedComponent(4, 0)
    val pcv = VisibleComponent(true)
    val pct = TypeComponent(EntityType.Material)
    val spw = SpawnerComponent(false)
    val playerCellEntity = PlayerCellEntity(pca, pcc, pcd, pcp, pcs, pcv, pct, spw)

    EntityManager.add(cellEntity)
    EntityManager.add(playerCellEntity)

    movementSystem.update()

    assert(cellEntity.getSpeedComponent == SpeedComponent(5.0, 1.0))
    assert(cellEntity.getPositionComponent.point == Point(5.0, 1.0))
    assert(cellEntity.getAccelerationComponent == AccelerationComponent(0.0, 0.0))

    assert(playerCellEntity.getSpeedComponent == SpeedComponent(0.0, -1.0))
    assert(playerCellEntity.getPositionComponent.point == Point(-4.0, 5.0))
    assert(playerCellEntity.getAccelerationComponent == AccelerationComponent(0.0, 0.0))
  }
}
