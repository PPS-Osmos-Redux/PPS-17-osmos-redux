package it.unibo.osmos.redux.mvc.view.components

import scalafx.geometry.Pos
import scalafx.scene.control.Button
import scalafx.scene.layout.VBox

/**
  * Center menu shown in MainMenu
  */
class MainMenuCenterBox(val listener: MainMenuCenterBoxListener) extends VBox {

  alignment = Pos.Center
  spacing = 10

  /* Play button */
  val playButton = new Button("Campaign Levels")
  /* Editor button */
  val editorButton = new Button("Editor Levels")
  /* Exit button */
  val exitButton = new Button("Exit")

  children = List(playButton, editorButton, exitButton)

  playButton.onAction = e => listener.onPlayClick()
  editorButton.onAction = e => listener.onEditorClick()
  exitButton.onAction = e => listener.onExitClick()
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
    * Called when the user clicks on the editor button
    */
  def onEditorClick()

  /**
    * Called when the user clicks on the exit button
    */
  def onExitClick()
}
