package it.unibo.osmos.redux.mvc.view.components.level

import scalafx.geometry.Insets
import scalafx.scene.control.Button
import scalafx.scene.layout.VBox

/**
  * Simple box to manage pause, resume and exit events
  */
class LevelStateBox(val listener: LevelStateBoxListener, val vSpacing: Double, val showPauseButton: Boolean = true) extends VBox(vSpacing){
  padding = Insets(4.0)

  /**
    * Pause button
    */
  private val pauseButton = new Button("Pause") {
    var isPaused: Boolean = false
    onAction = _ => {
      isPaused = !isPaused
      if (isPaused) {text = "Resume"; listener.onPause()} else {text = "Pause"; listener.onResume()}
    }
  }

  /**
    * Exit button
    */
  private val exitButton = new Button("Exit") {
    onAction = _ => listener.onExit()
  }

  if (showPauseButton) children = Seq(pauseButton, exitButton) else children = Seq(exitButton)
}

/**
  * Trait which gets notified when a LevelStateBox event occurs
  */
trait LevelStateBoxListener {

  /**
    * Called when the pause button is clicked
    */
  def onPause()

  /**
    * Called when the resume button is clicked
    */
  def onResume()

  /**
    * Called when the exit button is clicked
    */
  def onExit()
}
