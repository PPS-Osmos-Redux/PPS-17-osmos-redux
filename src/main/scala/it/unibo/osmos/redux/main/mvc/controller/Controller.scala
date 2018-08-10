package it.unibo.osmos.redux.main.mvc.controller
import spray.json._
import DefaultJsonProtocol._

import scala.io.Source
import it.unibo.osmos.redux.main.mvc.view.levels.LevelContext
import it.unibo.osmos.redux.main.ecs.entities.CellEntity
import it.unibo.osmos.redux.main.mvc.controller.Converters.getClass

import scala.collection.mutable
import scala.util.Try

/**
  * Controller base trait
  */
trait Controller {
  def startLevel(/*levelContext:LevelContext*/)
}


case class ControllerImpl() extends Controller {
  /*val engine:Engine*/
  override def startLevel(/*levelContext: LevelContext*/): Unit = {
    //1) load files
    val map:Map[String,String] = Map(JsCellEntity.getClass.getName -> "/level/JsCellEntity.txt", JsPlayerCellEntity.getClass.getName -> "/level/JsPlayerCellEntity.txt")
    var entities = loadEntities(map)
    //2) call init
    /*if(engine.isEmpty){
      engine = new Engine()
    }
    engine.init(levelContext,entities)*/
    //3) call start
    /*engine.start()*/
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
