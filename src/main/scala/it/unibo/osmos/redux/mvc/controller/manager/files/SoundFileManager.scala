package it.unibo.osmos.redux.mvc.controller.manager.files

import it.unibo.osmos.redux.utils.Constants.ResourcesPaths._

/**Defines operation on sounds files*/
object SoundFileManager extends FileManager {

  override implicit val who: String = "SoundFileManager"

  /** Gets menu music path
    *
    * @return menu music path to string
    */
  def loadMenuMusic(): String = getClass.getResource(SoundsPath + "MenuMusic.mp3").toURI toString

  /** Gets button sound path
    *
    * @return button sound path to string
    */
  def loadButtonsSound(): String = getClass.getResource(SoundsPath + "ButtonSound.mp3").toURI toString

  /** Gets level music path
    *
    * @return level music path to string
    */
  def loadLevelMusic(): String = getClass.getResource(SoundsPath + "LevelMusic.mp3").toURI toString
}
