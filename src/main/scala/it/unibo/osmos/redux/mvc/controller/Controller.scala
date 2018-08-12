package it.unibo.osmos.redux.mvc.controller
import spray.json._
import DefaultJsonProtocol._
import com.sun.xml.internal.ws.api.pipe.Engine
import it.unibo.osmos.redux.ecs.engine.GameEngine

import scala.io.Source
import it.unibo.osmos.redux.mvc.view.levels.LevelContext
import it.unibo.osmos.redux.ecs.entities.CellEntity
import it.unibo.osmos.redux.ecs.engine.GameEngine

import scala.util.Try

/**
  * Controller base trait
  */
trait Controller {
  def startLevel(levelContext:LevelContext)
}


case class ControllerImpl() extends Controller {
  var engine:Option[GameEngine] = None
  override def startLevel(levelContext: LevelContext): Unit = {
    //1) load files
    val map:Map[String,String] = Map(JsCellEntity.getClass.getName -> "/level/CellEntityDefinition.json", JsPlayerCellEntity.getClass.getName -> "/level/PlayerCellEntityDefinition.json")
    val entities = loadEntities(map)
    //2) call init
    engine match {
      case None => engine = Some(GameEngine())
      case _ =>
    }
    engine.get.init(levelContext,entities)
    //3) call start
    engine.get.start()
  }

  def loadEntities(filesToLoad:Map[String,String]):List[CellEntity] =  {
    var cellEntities:List[CellEntity] = List()
    filesToLoad.foreach(tuple => {
      val textFile = "" + FileManager.loadResource(tuple._2).getOrElse(println("File not found! Controller cannot load: ", tuple._2))
      import Converters._
      if(tuple._1.equals(JsPlayerCellEntity.getClass.getName)){
        cellEntities = textFile.parseJson.convertTo[List[JsPlayerCellEntity]].map(jsEntity => jsEntity.toPlayerCellEntity) ::: cellEntities
      } else if (tuple._1.equals( JsCellEntity.getClass.getName)){
        cellEntities = textFile.parseJson.convertTo[List[JsCellEntity]].map(jsEntity => jsEntity.toCellEntity) ::: cellEntities
      }
    })
    cellEntities
  }
}

object FileManager {
  /**
    * Reads a file from the resources folder
    * @param filename the name of the file
    * @return Content of the file
    */
  def loadResource(filename: String): Try[String] = Try(Source.fromInputStream(getClass.getResourceAsStream(filename)).mkString)
}
