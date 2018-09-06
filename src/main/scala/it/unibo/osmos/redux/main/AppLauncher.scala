package it.unibo.osmos.redux.main

import it.unibo.osmos.redux.mvc.controller._
import it.unibo.osmos.redux.mvc.controller.levels.{MultiPlayerLevels, SinglePlayerLevels}
import it.unibo.osmos.redux.mvc.controller.manager.files.{LevelFileManager, SettingsFileManger, UserProgressFileManager}
import it.unibo.osmos.redux.mvc.controller.manager.sounds.MusicPlayer
import it.unibo.osmos.redux.mvc.model.SettingsHolder
import it.unibo.osmos.redux.mvc.view.View
import scalafx.application.JFXApp

/**
  * Application entry point.
  */
object AppLauncher extends JFXApp {
  SettingsHolder.init(SettingsFileManger.loadSettings())

  SinglePlayerLevels.init(LevelFileManager.getLevelsConfigResourcesPath().getOrElse(List())
    .map(fileName => LevelFileManager.getResourceLevelInfo(fileName)))
  MultiPlayerLevels.init(LevelFileManager.getLevelsConfigResourcesPath(true).getOrElse(List())
    .map(fileName => LevelFileManager.getResourceLevelInfo(fileName)))

  SinglePlayerLevels.updateUserStat(UserProgressFileManager.loadUserProgress())
  val controller: Controller = new ControllerImpl
  MusicPlayer.setController(controller)
  val view = View(this)
  view.setController(controller)
}
