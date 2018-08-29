package it.unibo.osmos.redux.mvc.view.components.menu

import it.unibo.osmos.redux.mvc.view.components.custom.StyledButton
import scalafx.geometry.Pos
import scalafx.scene.control.Button
import scalafx.scene.layout.VBox

/**
  * Center menu shown in MainMenu
  */
class MainMenuCenterBox(val listener: MainMenuCenterBoxListener) extends VBox(20.0) {

  alignment = Pos.Center

  /* Single Player button */
  val singlePlayerButton = new StyledButton("Single Player")
  /* Multiplayer button */
  val multiplayerButton = new StyledButton("Multiplayer")
  /* Editor button */
  val levelEditorButton = new StyledButton("Level Editor")
  /* Editor button */
  val settingsButton = new StyledButton("Settings")
  /* Exit button */
  val exitButton = new StyledButton("Exit")

  children = List(singlePlayerButton, multiplayerButton, levelEditorButton, settingsButton, exitButton)

  singlePlayerButton.onAction = _ => listener.onPlayClick()
  multiplayerButton.onAction = _ => listener.onMultiPlayerClick()
  levelEditorButton.onAction = _ => listener.onEditorClick()
  settingsButton.onAction = _ => listener.onSettingsClick()
  exitButton.onAction = _ => listener.onExitClick()
}

/**
  * Trait which gets notified when a MainMenuCenterBox event occurs
  */
trait MainMenuCenterBoxListener {

  /** Called when the user clicks on the back to menu button */
  def backToMainMenu()

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
    * Called when the user clicks on the settings button
    */
  def onSettingsClick()

  /**
    * Called when the user clicks on the exit button
    */
  def onExitClick()
}
