package it.unibo.osmos.redux.mvc.view.components.menu

import it.unibo.osmos.redux.mvc.view.components.custom.StyledButton
import scalafx.geometry.Pos
import scalafx.scene.control.Button
import scalafx.scene.layout.VBox


/** Center menu shown in MainMenu
  *
  * @param listener the menu listener
  */
class MainMenuCenterBox(val listener: MainMenuCenterBoxListener) extends VBox(20.0) {

  alignment = Pos.Center

  /** Single-Player button */
  private val singlePlayerButton = new StyledButton("Single-Player")
  /** Multi-Player button */
  private val multiPlayerButton = new StyledButton("Multi-Player")
  /** Editor button */
  private val levelEditorButton = new StyledButton("Level Editor")
  /** Stats button */
  private val statsButton = new StyledButton("Stats")
  /** Editor button */
  private val settingsButton = new StyledButton("Settings")
  /** Exit button */
  private val exitButton = new StyledButton("Exit")

  children = List(singlePlayerButton, multiPlayerButton, levelEditorButton, statsButton, settingsButton, exitButton)

  singlePlayerButton.onAction = _ => listener.onPlayClick()
  multiPlayerButton.onAction = _ => listener.onMultiPlayerClick()
  levelEditorButton.onAction = _ => listener.onEditorClick()
  statsButton.onAction = _ => listener.onStatsClick()
  settingsButton.onAction = _ => listener.onSettingsClick()
  exitButton.onAction = _ => listener.onExitClick()
}

/**
  * Trait which gets notified when a MainMenuCenterBox event occurs
  */
trait MainMenuCenterBoxListener {

  /**
    * Called when the user clicks on the back to menu button
    */
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
    * Called when the user clicks on the settings button
    */
  def onSettingsClick()

  /**
    * Called when the user clicks on the exit button
    */
  def onExitClick()
}
