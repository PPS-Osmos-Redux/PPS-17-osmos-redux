package it.unibo.osmos.redux.multiplayer.server

object ServerState extends Enumeration {
  val Idle, Lobby, Game, Dead: ServerState.Value = Value
}
