package it.unibo.osmos.redux.mvc.controller.manager.files

import it.unibo.osmos.redux.utils.Constants.ResourcesPaths._

object SoundFileManager extends FileManager {

  override implicit val who: String = "SoundFileManager"

  /**
    * Gets menu music path
    * @return menu music string path
    */
  def loadMenuMusic(): String = getClass.getResource(SoundsPath + "MenuMusic.mp3").toURI toString

  /**
    * Gets button sound path
    * @return button sound string path
    */
  def loadButtonsSound(): String = getClass.getResource(SoundsPath + "ButtonSound.mp3").toURI toString

  /**
    * Gets level music path
    * @return level music string path
    */
  def loadLevelMusic(): String = getClass.getResource(SoundsPath + "LevelMusic.mp3").toURI toString
}
