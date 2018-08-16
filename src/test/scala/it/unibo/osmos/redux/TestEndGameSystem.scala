package it.unibo.osmos.redux

import it.unibo.osmos.redux.ecs.components._
import it.unibo.osmos.redux.ecs.entities.{CellEntity, EntityManager, PlayerCellEntity}
import it.unibo.osmos.redux.ecs.systems.EndGameSystem
import it.unibo.osmos.redux.mvc.model.VictoryRules
import it.unibo.osmos.redux.utils.Point
import org.scalatest.FunSuite

class TestEndGameSystem extends FunSuite {

  test("Test become the biggest victory"){
    val movementSystem = EndGameSystem(false, VictoryRules.becomeTheBiggest)
    EntityManager.subscribe(movementSystem, null)

    val sca = AccelerationComponent(0, 0)
    val scc = CollidableComponent(true)
    val scd = DimensionComponent(3)
    val scp = PositionComponent(Point(60, 64))
    val scs = SpeedComponent(0, 0)
    val scv = VisibleComponent(true)
    val sct = TypeComponent(EntityType.Material)
    val smallerCellEntity = CellEntity(sca, scc, scd, scp, scs, scv, sct)

    val bca = AccelerationComponent(0, 0)
    val bcc = CollidableComponent(true)
    val bcd = DimensionComponent(7)
    val bcp = PositionComponent(Point(100, 100))
    val bcs = SpeedComponent(0, 0)
    val bcv = VisibleComponent(true)
    val bct = TypeComponent(EntityType.Material)
    val biggerCellEntity = CellEntity(bca, bcc, bcd, bcp, bcs, bcv, bct)

    val pca = AccelerationComponent(0, 0)
    val pcc = CollidableComponent(true)
    val pcd = DimensionComponent(5)
    val pcp = PositionComponent(Point(50, 64))
    val pcs = SpeedComponent(4, 0)
    val pcv = VisibleComponent(true)
    val pct = TypeComponent(EntityType.Material)
    val spw = SpawnerComponent(false)
    val playerCellEntity = PlayerCellEntity(pca, pcc, pcd, pcp, pcs, pcv, pct, spw)

    EntityManager.add(smallerCellEntity)
    EntityManager.add(biggerCellEntity)
    EntityManager.add(playerCellEntity)

    movementSystem.update()

    // TODO
  }

  test("Test player death loss"){
    val movementSystem = EndGameSystem(false, VictoryRules.becomeTheBiggest)
    EntityManager.subscribe(movementSystem, null)

    val bca = AccelerationComponent(0, 0)
    val bcc = CollidableComponent(true)
    val bcd = DimensionComponent(7)
    val bcp = PositionComponent(Point(65, 64))
    val bcs = SpeedComponent(0, 0)
    val bcv = VisibleComponent(true)
    val bct = TypeComponent(EntityType.Material)
    val biggerCellEntity = CellEntity(bca, bcc, bcd, bcp, bcs, bcv, bct)

    val pca = AccelerationComponent(0, 0)
    val pcc = CollidableComponent(true)
    val pcd = DimensionComponent(5)
    val pcp = PositionComponent(Point(50, 64))
    val pcs = SpeedComponent(4, 0)
    val pcv = VisibleComponent(true)
    val pct = TypeComponent(EntityType.Material)
    val spw = SpawnerComponent(false)
    val playerCellEntity = PlayerCellEntity(pca, pcc, pcd, pcp, pcs, pcv, pct, spw)

    EntityManager.add(biggerCellEntity)
    EntityManager.add(playerCellEntity)

    movementSystem.update()

    // TODO
  }
}
