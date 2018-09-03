package it.unibo.osmos.redux.utils

object Constants {

  final val defaultSystemConfig: String = "application.conf"
  final val defaultSystemName: String = "OsmosReduxMultiplayerSystem"
  final val defaultServerActorName: String = "ServerActor"
  final val defaultClientActorName: String = "ClientActor"
  final val defaultClientUUID: String = "<unavailable>"
  final val defaultMaximumLobbyPlayers: Int = 3
  final val maxSpeed: Double = 4.0

  object Sentient {
    final val MAX_SPEED: Double = 2
    final val MAX_ACCELERATION: Double = 0.1
    final val COEFFICIENT_DESIRED_SEPARATION: Double = 50
    final val MIN_VALUE: Double = 1
    final val PERCENTAGE_OF_LOST_RADIUS_FOR_MAGNITUDE_ACCELERATION: Double = 0.02
    final val MIN_RADIUS_FOR_LOST_RADIUS_BEHAVIOUR = 15
    final val WEIGHT_OF_ESCAPE_ACCELERATION_FROM_ENEMIES: Double = 2
    final val WEIGHT_OF_ESCAPE_ACCELERATION_FROM_BOUNDARY: Double = WEIGHT_OF_ESCAPE_ACCELERATION_FROM_ENEMIES * 1.5
  }
}
