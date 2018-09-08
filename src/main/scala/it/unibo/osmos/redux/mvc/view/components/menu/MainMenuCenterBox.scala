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

  /* Single-Player button */
  val singlePlayerButton = new StyledButton("Single-Player")
  /* Multi-Player button */
  val multiPlayerButton = new StyledButton("Multi-Player")
  /* Editor button */
  val levelEditorButton = new StyledButton("Level Editor")
  /* Stats button */
  val statsButton = new StyledButton("Stats")
  /* Controls button */
  val controlsButton = new StyledButton("Controls")
  /* Editor button */
  val settingsButton = new StyledButton("Settings")
  /* Exit button */
  val exitButton = new StyledButton("Exit")

  children = List(singlePlayerButton, multiPlayerButton, levelEditorButton, statsButton, controlsButton, settingsButton, exitButton)

  singlePlayerButton.onAction = _ => listener.onPlayClick()
  multiPlayerButton.onAction = _ => listener.onMultiPlayerClick()
  levelEditorButton.onAction = _ => listener.onEditorClick()
  statsButton.onAction = _ => listener.onStatsClick()
  controlsButton.onAction = _ => listener.onControlsClick()
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
    * Called when the user clicks on the stats button
    */
  def onStatsClick()

  /**
    * Called when the user clicks on the controls button
    */
  def onControlsClick()

  /**
    * Called when the user clicks on the settings button
    */
  def onSettingsClick()

  /**
    * Called when the user clicks on the exit button
    */
  def onExitClick()
}
