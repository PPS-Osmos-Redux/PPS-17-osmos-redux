package it.unibo.osmos.redux

import it.unibo.osmos.redux.ecs.entities._
import it.unibo.osmos.redux.ecs.entities.builders.{CellBuilder, PlayerCellBuilder, SentientCellBuilder}
import it.unibo.osmos.redux.ecs.systems.EndGameSystem
import it.unibo.osmos.redux.mvc.model.{MapShape, VictoryRules}
import it.unibo.osmos.redux.mvc.view.context.{LevelContext, LevelContextListener}
import it.unibo.osmos.redux.mvc.view.drawables.DrawableWrapper
import it.unibo.osmos.redux.mvc.view.events.{GameLost, GamePending, GameWon}
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
    levelContext = LevelContext(isSimulation = false)
  }

  after {
    EntityManager.clear()
  }

  test("Become the biggest victory rule: the player wins having his entity radius greater than the other entities radius") {
    val endGameSystem = EndGameSystem(levelContext, VictoryRules.becomeTheBiggest)

    val smallerCellEntity = new CellBuilder().withDimension(4).withPosition(60, 64).build

    val playerCellEntity = PlayerCellBuilder().withDimension(6).withPosition(50, 64).build

    EntityManager.add(smallerCellEntity)
    EntityManager.add(playerCellEntity)

    assert(levelContext.gameCurrentState == GamePending)

    endGameSystem.update()

    assert(levelContext.gameCurrentState == GameWon)
  }

  test("Become the biggest victory rule: antimatter entities radius is ignored for the player victory") {
    val endGameSystem = EndGameSystem(levelContext, VictoryRules.becomeTheBiggest)

    val smallerCellEntity = new CellBuilder().withDimension(4).withPosition(60, 64).build

    val antimatterCellEntity = new CellBuilder().withDimension(10).withPosition(80, 84).withEntityType(EntityType.AntiMatter).build

    val playerCellEntity = PlayerCellBuilder().withDimension(6).withPosition(50, 64).build

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

    val cellEntity = new CellBuilder().withDimension(40).withPosition(160, 64).build

    val playerCellEntity = PlayerCellBuilder().withDimension(60).withPosition(50, 64).build

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

    val playerCellEntity = PlayerCellBuilder().withDimension(6).withPosition(50, 64).build

    val sentientCellEntity1 = SentientCellBuilder().withDimension(7).withPosition(65, 64).build

   val sentientCellEntity2 = SentientCellBuilder().withDimension(4).withPosition(80, 90).build

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

    val biggerCellEntity = new CellBuilder().withDimension(7).withPosition(65, 64).build

    EntityManager.add(biggerCellEntity)

    assert(levelContext.gameCurrentState == GamePending)

    endGameSystem.update()

    assert(levelContext.gameCurrentState == GameLost)
  }
}
