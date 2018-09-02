package travis.it.unibo.osmos.redux

import it.unibo.osmos.redux.ecs.components._
import it.unibo.osmos.redux.ecs.entities._
import it.unibo.osmos.redux.ecs.systems.SentientPrologSystem
import it.unibo.osmos.redux.utils.Point
import org.scalatest.{BeforeAndAfter, FunSuite}

class TestSentientPrologSystem extends FunSuite with BeforeAndAfter {

  after {
    EntityManager.clear()
  }

  test("Sentient cell hunting smaller cells") {
    val sentientPrologSystem = SentientPrologSystem()
    EntityManager.subscribe(sentientPrologSystem, null)

    val spawner = SpawnerComponent(false)
    val sca = AccelerationComponent(0, 0)
    val scc = CollidableComponent(true)
    val scd = DimensionComponent(10)
    val scp = PositionComponent(Point(50, 64))
    val scs = SpeedComponent(0, 0)
    val scv = VisibleComponent(true)
    val sentientCellEntity = SentientCellEntity(sca, scc, scd, scp, scs, scv, spawner)

    val sca2 = AccelerationComponent(0, 0)
    val scc2 = CollidableComponent(true)
    val scd2 = DimensionComponent(8)
    val scp2 = PositionComponent(Point(101, 87))
    val scs2 = SpeedComponent(0, 0)
    val scv2 = VisibleComponent(true)
    val sentientCellEntity2 = SentientCellEntity(sca2, scc2, scd2, scp2, scs2, scv2, spawner)

    val ca1 = AccelerationComponent(1, 1)
    val cc1 = CollidableComponent(true)
    val cd1 = DimensionComponent(4)
    val cp1 = PositionComponent(Point(100, 170))
    val cs1 = SpeedComponent(4, 5)
    val cv1 = VisibleComponent(true)
    val ct1 = TypeComponent(EntityType.Matter)
    val cellEntity1 = CellEntity(ca1, cc1, cd1, cp1, cs1, cv1, ct1)

    val ca2 = AccelerationComponent(1, 1)
    val cc2 = CollidableComponent(true)
    val cd2 = DimensionComponent(8)
    val cp2 = PositionComponent(Point(30, 100))
    val cs2 = SpeedComponent(0, 0)
    val cv2 = VisibleComponent(true)
    val ct2 = TypeComponent(EntityType.Matter)
    val cellEntity2 = CellEntity(ca2, cc2, cd2, cp2, cs2, cv2, ct2)

    val ca3 = AccelerationComponent(1, 1)
    val cc3 = CollidableComponent(true)
    val cd3 = DimensionComponent(7)
    val cp3 = PositionComponent(Point(130, 40))
    val cs3 = SpeedComponent(4, 3)
    val cv3 = VisibleComponent(true)
    val ct3 = TypeComponent(EntityType.Matter)
    val cellEntity3 = CellEntity(ca3, cc3, cd3, cp3, cs3, cv3, ct3)

    EntityManager.add(sentientCellEntity)
    EntityManager.add(sentientCellEntity2)
    EntityManager.add(cellEntity1)
    EntityManager.add(cellEntity2)
    EntityManager.add(cellEntity3)


    sentientPrologSystem.update()
  }

  /*test("Sentient cell running from bigger cells") {
    // TODO
    val sentientPrologSystem = SentientPrologSystem()
    EntityManager.subscribe(sentientPrologSystem, null)

    sentientPrologSystem.update()
  }*/
}
