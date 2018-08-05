package it.unibo.osmos.redux.main.mvc.view.menus

import scalafx.scene.control.{Menu, MenuBar, MenuItem}

/**
  * MenuBar showed at the top of the MainScene
  */
class MainMenuBar(val listener: MainMenuBarListener) extends MenuBar {
  /* Settings menu */
  val settingsMenu = new Menu("Settings")
  /* Settings - Fullscreen item */
  val fullscreenMenuItem = new MenuItem("Enable Fullscreen")
  fullscreenMenuItem.onAction = e => listener.onFullScreenSettingClick()
  settingsMenu.items = List(fullscreenMenuItem)

  /* Adding all the items */
  menus = List(settingsMenu)
}

/**
  * Trait which gets notified when a MainMenuBar event occurs
  */
trait MainMenuBarListener {

  /**
    * Called when the user clicks on the fullScreen button
    */
  def onFullScreenSettingClick()

}

