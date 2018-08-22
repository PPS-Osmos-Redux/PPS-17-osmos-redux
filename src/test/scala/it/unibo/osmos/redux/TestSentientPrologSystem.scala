package it.unibo.osmos.redux

import it.unibo.osmos.redux.ecs.components._
import it.unibo.osmos.redux.ecs.entities.{CellEntity, EntityManager, PlayerCellEntity, SentientCellEntity}
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

    val sca = AccelerationComponent(0, 0)
    val scc = CollidableComponent(true)
    val scd = DimensionComponent(6)
    val scp = PositionComponent(Point(50, 64))
    val scs = SpeedComponent(0, 0)
    val scv = VisibleComponent(true)
    val sentientCellEntity = SentientCellEntity(sca, scc, scd, scp, scs, scv)

    val ca = AccelerationComponent(0, 0)
    val cc = CollidableComponent(true)
    val cd = DimensionComponent(4)
    val cp = PositionComponent(Point(60, 64))
    val cs = SpeedComponent(0, 0)
    val cv = VisibleComponent(true)
    val ct = TypeComponent(EntityType.Matter)
    val smallerCellEntity = CellEntity(ca, cc, cd, cp, cs, cv, ct)

    val ca2 = AccelerationComponent(0, 0)
    val cc2 = CollidableComponent(true)
    val cd2 = DimensionComponent(5)
    val cp2 = PositionComponent(Point(60, 64))
    val cs2 = SpeedComponent(0, 0)
    val cv2 = VisibleComponent(true)
    val ct2 = TypeComponent(EntityType.Matter)
    val smallerCellEntity2 = CellEntity(ca2, cc2, cd2, cp2, cs2, cv2, ct2)

    EntityManager.add(sentientCellEntity)
    EntityManager.add(smallerCellEntity)
    EntityManager.add(smallerCellEntity2)


    sentientPrologSystem.update()
  }

  /*test("Sentient cell running from bigger cells") {
    // TODO
    val sentientPrologSystem = SentientPrologSystem()
    EntityManager.subscribe(sentientPrologSystem, null)

    sentientPrologSystem.update()
  }*/
}
