package it.unibo.osmos.redux

import it.unibo.osmos.redux.ecs.components._
import it.unibo.osmos.redux.ecs.entities.{CellEntity, EntityManager, PlayerCellEntity, SentientCellEntity}
import it.unibo.osmos.redux.ecs.systems.EndGameSystem
import it.unibo.osmos.redux.mvc.model.{MapShape, VictoryRules}
import it.unibo.osmos.redux.mvc.view.drawables.DrawableWrapper
import it.unibo.osmos.redux.mvc.view.events.{GameLost, GamePending, GameWon}
import it.unibo.osmos.redux.mvc.view.levels.{LevelContext, LevelContextListener}
import it.unibo.osmos.redux.utils.Point
import org.scalatest.{BeforeAndAfter, FunSuite}

import scala.collection.mutable.ListBuffer

class TestEndGameSystem extends FunSuite with BeforeAndAfter {

  private val levelContextListener = new LevelContextListener {
    override def onDrawEntities(playerEntity: Option[DrawableWrapper], entities: Seq[DrawableWrapper]): Unit = {}

    override def onLevelSetup(mapShape: MapShape): Unit = {}

    override def onLevelEnd(levelResult: Boolean): Unit = {}
  }
  private var levelContext: LevelContext = _

  before {
    levelContext = LevelContext(levelContextListener, true)
  }

  after {
    EntityManager.clear()
  }

  test("Become the biggest victory rule: the player wins having his entity radius greater than the other entities radius") {
    val endGameSystem = EndGameSystem(levelContext, VictoryRules.becomeTheBiggest)

    val sca = AccelerationComponent(0, 0)
    val scc = CollidableComponent(true)
    val scd = DimensionComponent(4)
    val scp = PositionComponent(Point(60, 64))
    val scs = SpeedComponent(0, 0)
    val scv = VisibleComponent(true)
    val sct = TypeComponent(EntityType.Matter)
    val smallerCellEntity = CellEntity(sca, scc, scd, scp, scs, scv, sct)

    val pca = AccelerationComponent(0, 0)
    val pcc = CollidableComponent(true)
    val pcd = DimensionComponent(6)
    val pcp = PositionComponent(Point(50, 64))
    val pcs = SpeedComponent(4, 0)
    val pcv = VisibleComponent(true)
    val pct = TypeComponent(EntityType.Matter)
    val spw = SpawnerComponent(false)
    val playerCellEntity = PlayerCellEntity(pca, pcc, pcd, pcp, pcs, pcv, pct, spw)

    EntityManager.add(smallerCellEntity)
    EntityManager.add(playerCellEntity)

    assert(levelContext.gameCurrentState == GamePending)

    endGameSystem.update()

    assert(levelContext.gameCurrentState == GameWon)
  }

  test("Become the biggest victory rule: antimatter entities radius is ignored for the player victory") {
    val endGameSystem = EndGameSystem(levelContext, VictoryRules.becomeTheBiggest)

    val sca = AccelerationComponent(0, 0)
    val scc = CollidableComponent(true)
    val scd = DimensionComponent(4)
    val scp = PositionComponent(Point(60, 64))
    val scs = SpeedComponent(0, 0)
    val scv = VisibleComponent(true)
    val sct = TypeComponent(EntityType.Matter)
    val smallerCellEntity = CellEntity(sca, scc, scd, scp, scs, scv, sct)

    val aca = AccelerationComponent(0, 0)
    val acc = CollidableComponent(true)
    val acd = DimensionComponent(10)
    val acp = PositionComponent(Point(80, 84))
    val acs = SpeedComponent(0, 0)
    val acv = VisibleComponent(true)
    val act = TypeComponent(EntityType.AntiMatter)
    val antimatterCellEntity = CellEntity(aca, acc, acd, acp, acs, acv, act)

    val pca = AccelerationComponent(0, 0)
    val pcc = CollidableComponent(true)
    val pcd = DimensionComponent(6)
    val pcp = PositionComponent(Point(50, 64))
    val pcs = SpeedComponent(4, 0)
    val pcv = VisibleComponent(true)
    val pct = TypeComponent(EntityType.Matter)
    val spw = SpawnerComponent(false)
    val playerCellEntity = PlayerCellEntity(pca, pcc, pcd, pcp, pcs, pcv, pct, spw)

    EntityManager.add(smallerCellEntity)
    EntityManager.add(antimatterCellEntity)
    EntityManager.add(playerCellEntity)

    assert(levelContext.gameCurrentState == GamePending)

    endGameSystem.update()

    assert(levelContext.gameCurrentState == GameWon)
  }

  test("Become huge victory: the player wins after having his entity radius greater than a certain percentage of the total entities radius") {
    val endGameSystem = EndGameSystem(levelContext, VictoryRules.becomeHuge)

    val entityList: ListBuffer[CellEntity] = ListBuffer()

    val ca = AccelerationComponent(0, 0)
    val cc = CollidableComponent(true)
    val cd = DimensionComponent(40)
    val cp = PositionComponent(Point(160, 64))
    val cs = SpeedComponent(0, 0)
    val cv = VisibleComponent(true)
    val ct = TypeComponent(EntityType.Matter)
    val cellEntity = CellEntity(ca, cc, cd, cp, cs, cv, ct)

    val pca = AccelerationComponent(0, 0)
    val pcc = CollidableComponent(true)
    val pcd = DimensionComponent(60)
    val pcp = PositionComponent(Point(50, 64))
    val pcs = SpeedComponent(0, 0)
    val pcv = VisibleComponent(true)
    val pct = TypeComponent(EntityType.Matter)
    val spw = SpawnerComponent(false)
    val playerCellEntity = PlayerCellEntity(pca, pcc, pcd, pcp, pcs, pcv, pct, spw)

    entityList += cellEntity
    entityList += playerCellEntity

    var totalRadius = 0.0
    entityList foreach (e => {
      totalRadius += e.getDimensionComponent.radius
    })

    entityList foreach (e => EntityManager.add(e))

    assert(levelContext.gameCurrentState == GamePending)

    endGameSystem.update()
    assert(levelContext.gameCurrentState == GamePending)
    cellEntity.getDimensionComponent.radius_(totalRadius * 30 / 100 - 1)
    playerCellEntity.getDimensionComponent.radius_(totalRadius * 70 / 100 + 1)

    endGameSystem.update()
    assert(levelContext.gameCurrentState == GameWon)
  }

  test("Absorb hostile cells victory rule: the player wins after no more sentient entities are alive") {
    val endGameSystem = EndGameSystem(levelContext, VictoryRules.absorbTheHostileCells)

    val pca = AccelerationComponent(0, 0)
    val pcc = CollidableComponent(true)
    val pcd = DimensionComponent(6)
    val pcp = PositionComponent(Point(50, 64))
    val pcs = SpeedComponent(4, 0)
    val pcv = VisibleComponent(true)
    val pct = TypeComponent(EntityType.Matter)
    val spw = SpawnerComponent(false)
    val playerCellEntity = PlayerCellEntity(pca, pcc, pcd, pcp, pcs, pcv, pct, spw)

    val sca1 = AccelerationComponent(0, 0)
    val scc1 = CollidableComponent(true)
    val scd1 = DimensionComponent(7)
    val scp1 = PositionComponent(Point(65, 64))
    val scs1 = SpeedComponent(0, 0)
    val scv1 = VisibleComponent(true)
    val sentientCellEntity1 = SentientCellEntity(sca1, scc1, scd1, scp1, scs1, scv1)

    val sca2 = AccelerationComponent(0, 0)
    val scc2 = CollidableComponent(true)
    val scd2 = DimensionComponent(4)
    val scp2 = PositionComponent(Point(80, 90))
    val scs2 = SpeedComponent(0, 0)
    val scv2 = VisibleComponent(true)
    val sentientCellEntity2 = SentientCellEntity(sca2, scc2, scd2, scp2, scs2, scv2)

    EntityManager.add(playerCellEntity)
    EntityManager.add(sentientCellEntity1)
    EntityManager.add(sentientCellEntity2)

    assert(levelContext.gameCurrentState == GamePending)

    EntityManager.delete(sentientCellEntity1)
    endGameSystem.update()
    assert(levelContext.gameCurrentState == GamePending)

    EntityManager.delete(sentientCellEntity2)
    endGameSystem.update()
    assert(levelContext.gameCurrentState == GameWon)
  }

  test("The game is lost after the player's entity is not alive") {
    val endGameSystem = EndGameSystem(levelContext, VictoryRules.becomeTheBiggest)

    val bca = AccelerationComponent(0, 0)
    val bcc = CollidableComponent(true)
    val bcd = DimensionComponent(7)
    val bcp = PositionComponent(Point(65, 64))
    val bcs = SpeedComponent(0, 0)
    val bcv = VisibleComponent(true)
    val bct = TypeComponent(EntityType.Matter)
    val biggerCellEntity = CellEntity(bca, bcc, bcd, bcp, bcs, bcv, bct)

    EntityManager.add(biggerCellEntity)

    assert(levelContext.gameCurrentState == GamePending)

    endGameSystem.update()

    assert(levelContext.gameCurrentState == GameLost)
  }
}
