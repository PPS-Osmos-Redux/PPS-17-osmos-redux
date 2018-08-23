package it.unibo.osmos.redux.mvc.view.components.level

import scalafx.geometry.Insets
import scalafx.scene.control.Button
import scalafx.scene.layout.VBox

/**
  * Simple box to manage pause, resume and exit events
  */
class LevelStateBox(val listener: LevelStateBoxListener, val vSpacing: Double) extends VBox(vSpacing){
  padding = Insets(4.0)
  children = Seq(new Button("Pause") {
    var isPaused: Boolean = false

    onAction = _ => {
      isPaused = !isPaused
      if (isPaused) {text = "Resume"; listener.onPause()} else {text = "Pause"; listener.onResume()}
    }

  }, new Button("Exit") {
    onAction = _ => listener.onExit()
  })
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
