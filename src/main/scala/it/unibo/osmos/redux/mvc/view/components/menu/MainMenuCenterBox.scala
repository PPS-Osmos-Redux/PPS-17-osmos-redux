package it.unibo.osmos.redux.mvc.view.components.menu

import scalafx.geometry.Pos
import scalafx.scene.control.Button
import scalafx.scene.layout.VBox

/**
  * Center menu shown in MainMenu
  */
class MainMenuCenterBox(val listener: MainMenuCenterBoxListener) extends VBox(20.0) {

  alignment = Pos.Center

  /* Play button */
  val playButton = new Button("Campaign Levels")
  /* Multiplayer button */
  val multiplayerButton = new Button("Multiplayer")
  /* Editor button */
  val editorButton = new Button("Editor Levels")
  /* Exit button */
  val exitButton = new Button("Exit")

  children = List(playButton, multiplayerButton, editorButton, exitButton)

  playButton.onAction = _ => listener.onPlayClick()
  multiplayerButton.onAction = _ => listener.onMultiPlayerClick()
  editorButton.onAction = _ => listener.onEditorClick()
  exitButton.onAction = _ => listener.onExitClick()
}

/**
  * Trait which gets notified when a MainMenuCenterBox event occurs
  */
trait MainMenuCenterBoxListener {

  /**
    * Called when the user clicks on the play button
    */
  def onPlayClick()

  /**
    * Called when the user clicks on the multiplayer button
    */
  def onMultiPlayerClick()

  /**
    * Called when the user clicks on the editor button
    */
  def onEditorClick()

  /**
    * Called when the user clicks on the exit button
    */
  def onExitClick()
}
