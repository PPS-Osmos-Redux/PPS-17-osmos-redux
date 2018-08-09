package it.unibo.osmos.redux.main.mvc.controller

import it.unibo.osmos.redux.main.ecs.components._
import it.unibo.osmos.redux.main.ecs.entities.{CellEntity, PlayerCellEntity}
import it.unibo.osmos.redux.main.mvc.view.levels.LevelContext
import it.unibo.osmos.redux.main.utils.Point

/**
  * Controller base trait
  */
trait Controller {
  def startLevel(levelContext:LevelContext)
}


case class ControllerImpl(/*engine:Engine*/) extends Controller {
  override def startLevel(levelContext: LevelContext): Unit = {
    //1) load files
    val entities = loadEntities()
    //2) call init
    /*engine.init(levelContext,entities)*/

    //3) call start
    /*engine.start()*/
  }
  
  def loadEntities():List[CellEntity] = {
    val a = AccelerationComponent(1, 1)
    val c = CollidableComponent(true)
    val d = DimensionComponent(10)
    val dPlayer = DimensionComponent(20)
    val p = PositionComponent(Point(20, 20))
    val pPlayer = PositionComponent(Point(50, 50))
    val s = SpeedComponent(4, 0)
    val v = VisibleComponent(true)
    val ce = CellEntity(a, c, d, p, s, v)
    val pce = PlayerCellEntity(a,c,dPlayer,pPlayer,s,v)
    List(ce,pce)
  }
}
