package it.unibo.osmos.redux.mvc.view.components.level

import scalafx.geometry.Insets
import scalafx.scene.control.Button
import scalafx.scene.layout.VBox

/** Simple box to manage resume and exit events
  *
  * @param listener the LevelStateBoxListener
  * @param vSpacing the spacing between the elements
  */
class LevelStateBox(val listener: LevelStateBoxListener, val vSpacing: Double) extends VBox(vSpacing){
  padding = Insets(4.0)
  visible = true
  margin = Insets(30.0)

  /** Resume button */
  private val resumeButton = new Button("Resume") {
    onAction = _ => listener.onResume()
  }

  /** Exit button */
  private val exitButton = new Button("Exit") {
    onAction = _ => listener.onExit()
  }

  Seq(resumeButton, exitButton)
}

/**
  * Trait which gets notified when a LevelStateBox event occurs
  */
trait LevelStateBoxListener {

  /**
    * Called when the resume button is clicked
    */
  def onResume()

  /**
    * Called when the exit button is clicked
    */
  def onExit()
}
