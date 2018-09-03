package it.unibo.osmos.redux.main

import it.unibo.osmos.redux.mvc.controller.{Controller, ControllerImpl, FileManager, MediaPlayer}
import it.unibo.osmos.redux.mvc.model.SinglePlayerLevels
import it.unibo.osmos.redux.mvc.view.View
import scalafx.application.JFXApp

/**
  * Application entry point.
  */
object AppLauncher extends JFXApp {
  SinglePlayerLevels.updateUserStat(FileManager.loadUserProgress())
  val controller: Controller = new ControllerImpl
  MediaPlayer.setController(controller)
  val view = View(this)
  view.setController(controller)
}
