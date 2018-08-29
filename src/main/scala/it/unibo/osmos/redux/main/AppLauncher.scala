package it.unibo.osmos.redux.main

import it.unibo.osmos.redux.mvc.controller.{Controller, ControllerImpl, FileManager}
import it.unibo.osmos.redux.mvc.model.{Model, SinglePlayerLevels}
import it.unibo.osmos.redux.mvc.view.View
import scalafx.application.JFXApp

/**
  * Application entry point.
  */
object AppLauncher extends JFXApp {

  //TODO: replace null with proper implementation
  val model: Model = null
  SinglePlayerLevels.updateUserStat(FileManager.loadUserProgress())
  val controller: Controller = new ControllerImpl
  val view = View(this)
  view.setController(controller)
}
