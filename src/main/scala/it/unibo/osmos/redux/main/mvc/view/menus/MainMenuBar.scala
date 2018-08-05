package it.unibo.osmos.redux.main.mvc.view.menus

import scalafx.scene.control.{Menu, MenuBar, MenuItem}

/**
  * MenuBar showed at the top of the MainScene
  */
class MainMenuBar extends MenuBar {
    val fullscreenMenuItem = new MenuItem("Enable fullscreen")
    val settingsMenu = new Menu("Settings")
    settingsMenu.items = List(fullscreenMenuItem)

    menus = List(settingsMenu)
}
