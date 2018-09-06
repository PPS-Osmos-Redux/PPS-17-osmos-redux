package it.unibo.osmos.redux.main

import it.unibo.osmos.redux.mvc.controller._
import it.unibo.osmos.redux.mvc.controller.levels.manager.SinglePlayerLevels
import it.unibo.osmos.redux.mvc.controller.manager.files.UserProgressFileManager
import it.unibo.osmos.redux.mvc.controller.manager.sounds.MusicPlayer
import it.unibo.osmos.redux.mvc.view.View
import scalafx.application.JFXApp

/**
  * Application entry point.
  */
object AppLauncher extends JFXApp {
  SinglePlayerLevels.updateUserStat(UserProgressFileManager.loadUserProgress())
  val controller: Controller = new ControllerImpl
  MusicPlayer.setController(controller)
  val view = View(this)
  view.setController(controller)
}
