package it.unibo.osmos.redux.mvc.view.context

import it.unibo.osmos.redux.mvc.view.components.multiplayer.User

trait LobbyContext extends {

  def getCurrentUsers: Seq[User]

}
