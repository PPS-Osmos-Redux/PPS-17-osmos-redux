package it.unibo.osmos.redux.multiplayer.lobby

import it.unibo.osmos.redux.multiplayer.players.Player
import it.unibo.osmos.redux.utils.Constants

import scala.collection.mutable

trait Lobby[T <: Player] {

  /**
    * The map that holds the players.
    */
  protected var players: mutable.Map[String, T] = mutable.Map()

  /**
    * The maximum number of players in the lobby.
    */
  protected val maximumNumberOfPlayers = Constants.defaultMaximumLobbyPlayers

  /**
    * Adds a new player.
    * @param player The player
    */
  def addPlayer(player: T): Unit

  /**
    * Adds new players.
    * @param players The list of players
    */
  def addPlayers(players: T*): Unit

  /**
    * Gets a player.
    * @param username The username of the player to get
    * @return The player if it's found, otherwise none.
    */
  def getPlayer(username: String): Option[T]

  /**
    * Gets all players.
    * @return The set of all players.
    */
  def getPlayers: Seq[T]

  /**
    * Removes a player.
    * @param username The username of the player to remove.
    */
  def removePlayer(username: String): Unit

  /**
    * Defines if the lobby is full or not.
    * @return True if the lobby is full; otherwise false.
    */
  def isFull: Boolean = players.size == maximumNumberOfPlayers

  /**
    * Clears this instance.
    */
  def clear(): Unit = players.clear()
}





