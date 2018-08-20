package it.unibo.osmos.redux

import it.unibo.osmos.redux.ecs.components._
import it.unibo.osmos.redux.ecs.entities.{CellEntity, EntityManager, PlayerCellEntity}
import it.unibo.osmos.redux.ecs.systems.EndGameSystem
import it.unibo.osmos.redux.mvc.model.{MapShape, VictoryRules}
import it.unibo.osmos.redux.mvc.view.drawables.DrawableWrapper
import it.unibo.osmos.redux.mvc.view.events.{GameLost, GamePending, GameWon}
import it.unibo.osmos.redux.mvc.view.levels.{LevelContext, LevelContextListener}
import it.unibo.osmos.redux.utils.Point
import org.scalatest.{BeforeAndAfter, FunSuite}

class TestEndGameSystem extends FunSuite with BeforeAndAfter {

  private var levelContext: LevelContext = _
  private var endGameSystem: EndGameSystem = _

  before {
    levelContext = LevelContext(levelContextListener, true)
  }

  after {
    EntityManager.clear()
  }

  private val levelContextListener = new LevelContextListener {
    override def onDrawEntities(playerEntity: Option[DrawableWrapper], entities: Seq[DrawableWrapper]): Unit = {}

    override def onLevelSetup(mapShape: MapShape): Unit = {}

    override def onLevelEnd(levelResult: Boolean): Unit = {}
  }

  private def initEntityManager(victoryRules: VictoryRules.Value) {
    endGameSystem = EndGameSystem(levelContext, victoryRules)
    EntityManager.subscribe(endGameSystem, null)
  }

  test("Test become the biggest victory") {
    initEntityManager(VictoryRules.becomeTheBiggest)

    val sca = AccelerationComponent(0, 0)
    val scc = CollidableComponent(true)
    val scd = DimensionComponent(4)
    val scp = PositionComponent(Point(60, 64))
    val scs = SpeedComponent(0, 0)
    val scv = VisibleComponent(true)
    val sct = TypeComponent(EntityType.Material)
    val smallerCellEntity = CellEntity(sca, scc, scd, scp, scs, scv, sct)

    val pca = AccelerationComponent(0, 0)
    val pcc = CollidableComponent(true)
    val pcd = DimensionComponent(6)
    val pcp = PositionComponent(Point(50, 64))
    val pcs = SpeedComponent(4, 0)
    val pcv = VisibleComponent(true)
    val pct = TypeComponent(EntityType.Material)
    val spw = SpawnerComponent(false)
    val playerCellEntity = PlayerCellEntity(pca, pcc, pcd, pcp, pcs, pcv, pct, spw)

    EntityManager.add(smallerCellEntity)
    EntityManager.add(playerCellEntity)

    assert(levelContext.gameCurrentState == GamePending)

    endGameSystem.update()

    assert(levelContext.gameCurrentState == GameWon)
  }

  test("Test become huge victory") {
    // TODO
    /*initEntityManager(VictoryRules.becomeTheBiggest)

    val sca = AccelerationComponent(0, 0)
    val scc = CollidableComponent(true)
    val scd = DimensionComponent(4)
    val scp = PositionComponent(Point(60, 64))
    val scs = SpeedComponent(0, 0)
    val scv = VisibleComponent(true)
    val sct = TypeComponent(EntityType.Material)
    val smallerCellEntity = CellEntity(sca, scc, scd, scp, scs, scv, sct)

    val pca = AccelerationComponent(0, 0)
    val pcc = CollidableComponent(true)
    val pcd = DimensionComponent(6)
    val pcp = PositionComponent(Point(50, 64))
    val pcs = SpeedComponent(4, 0)
    val pcv = VisibleComponent(true)
    val pct = TypeComponent(EntityType.Material)
    val spw = SpawnerComponent(false)
    val playerCellEntity = PlayerCellEntity(pca, pcc, pcd, pcp, pcs, pcv, pct, spw)

    EntityManager.add(smallerCellEntity)
    EntityManager.add(playerCellEntity)

    assert(levelContext.gameCurrentState == GamePending)

    endGameSystem.update()

    assert(levelContext.gameCurrentState == GameWon)*/
  }

  test("Test player death loss") {
    initEntityManager(VictoryRules.becomeTheBiggest)

    val bca = AccelerationComponent(0, 0)
    val bcc = CollidableComponent(true)
    val bcd = DimensionComponent(7)
    val bcp = PositionComponent(Point(65, 64))
    val bcs = SpeedComponent(0, 0)
    val bcv = VisibleComponent(true)
    val bct = TypeComponent(EntityType.Material)
    val biggerCellEntity = CellEntity(bca, bcc, bcd, bcp, bcs, bcv, bct)

    EntityManager.add(biggerCellEntity)

    assert(levelContext.gameCurrentState == GamePending)

    endGameSystem.update()

    assert(levelContext.gameCurrentState == GameLost)
  }
}
