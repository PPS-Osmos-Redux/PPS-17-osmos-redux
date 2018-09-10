package it.unibo.osmos.redux.utils

import java.nio.file.{FileSystem, FileSystems}

object Constants {

  object Game {
    final val defaultFps: Int = 60
    final val minimumFps: Int = 30
    final val maximumFps: Int = 240
    final val fpsChangeStep: Int = 30
  }

  object MultiPlayer {
    final val defaultSystemConfig: String = "akka-config/kryo.conf"
    final val defaultSystemName: String = "Osmos-Redux-MultiPlayer-System"
    final val defaultServerActorName: String = "ServerActor"
    final val defaultClientActorName: String = "ClientActor"
    final val defaultClientUUID: String = "<unavailable>"
    final val defaultMaximumLobbyPlayers: Int = 8
    final val defaultMultiPlayerFps: Int = 30
  }

  object Sentient {
    final val MaxSpeed: Double = 2
    final val MaxAcceleration: Double = 0.1
    //extra radius for safety area
    final val CoefficientDesiredSeparation: Double = 50
    final val PercentageOfLostRadiusForMagnitudeAcceleration: Double = 0.02
    final val MinRadiusForLostRadiusBehaviour: Double = 15
    final val WeightOfEscapeAccelerationFromEnemies: Double = 2
    final val WeightOfEscapeAccelerationFromBoundary: Double = WeightOfEscapeAccelerationFromEnemies * 1.5
  }

  object ResourcesPaths {
    final val Separator: String = "/"
    final val LevelStartPath: String = Separator + "levels"
    final val SinglePlayerLevelsPath: String = LevelStartPath + Separator + "singlePlayer" + Separator
    final val MultiPlayerLevelsPath: String = LevelStartPath + Separator + "multiPlayer" + Separator
    final val ConfigSinglePlayer: String = LevelStartPath + Separator + "config" + Separator + "SinglePlayerLevels"
    final val ConfigMultiPlayer: String = LevelStartPath + Separator + "config" + Separator + "MultiPlayerLevels"
    final val SoundsPath: String = Separator + "sounds" + Separator
    final val StylePath: String = Separator + "style" + Separator + "style.css"
  }

  object UserHomePaths {
    final val DefaultFS: FileSystem = FileSystems.getDefault
    final val SystemSeparator: String = DefaultFS.getSeparator
    final val UserHome: String = System.getProperty("user.home")
    final val GameDirectory: String = ".Osmos-Redux" + SystemSeparator
    final val LevelsDirectory: String = UserHome + SystemSeparator + GameDirectory + "CustomLevels" + SystemSeparator
    final val UserProgressDirectory: String = UserHome + SystemSeparator + GameDirectory +
      "UserProgress" + SystemSeparator
    final val UserProgressFileName: String = UserProgressDirectory + "UserProgress"
    final val SettingFilePath: String = UserHome + SystemSeparator + GameDirectory + "Settings" + SystemSeparator + "GeneralSettings"
  }

}
