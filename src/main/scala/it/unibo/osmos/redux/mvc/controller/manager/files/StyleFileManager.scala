package it.unibo.osmos.redux.mvc.controller.manager.files

import it.unibo.osmos.redux.utils.Logger

object StyleFileManager extends FileManager {

  override implicit val who: String = "StyleFileManager"

  def getStyle: String = {
    try {
      val url = getClass.getResource("/style/style.css")
      //println("style url: " + url)
      url.toString
    } catch {
      case _: NullPointerException =>
        Logger.log("Error: style.css file not found")
        throw new NullPointerException("style.css file not found");
    }
  }
}
