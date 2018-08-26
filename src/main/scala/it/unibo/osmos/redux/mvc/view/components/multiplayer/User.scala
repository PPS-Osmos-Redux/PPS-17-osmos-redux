package it.unibo.osmos.redux.mvc.view.components.multiplayer

import it.unibo.osmos.redux.multiplayer.players.BasicPlayer
import scalafx.beans.property.{BooleanProperty, IntegerProperty, StringProperty}

/**
  * User class
  *
  * @param username the username
  * @param ip the ip
  * @param port the port
  * @param isServer true if the user is a server, false if it's a client
  */
case class User(username: String, ip: String = "", port: Int = 0, isServer: Boolean) {

  /**
    * Secondary constructor.
    * @param player The BasicPlayer where to get user info from
    * @param isServer true if the user is a server, false if it's a client
    * @return
    */
  def this(player: BasicPlayer, isServer: Boolean) = this(player.getUsername, player.getInfo.address, player.getInfo.port, isServer)

  def getUserWithProperty: UserWithProperties = UserWithProperties(StringProperty(username), StringProperty(ip), IntegerProperty(port), BooleanProperty(isServer))
}

/**
  * User class with properties as base fields
  *
  * @param username the username
  * @param ip the ip
  * @param port the port
  * @param isServer true if the user is a server, false if it's a client
  */
case class UserWithProperties(username: StringProperty, ip: StringProperty = StringProperty(""), port: IntegerProperty = IntegerProperty(0), isServer: BooleanProperty) {

  def getUser: User = User(username.value, ip.value, port.value, isServer.value)

}
