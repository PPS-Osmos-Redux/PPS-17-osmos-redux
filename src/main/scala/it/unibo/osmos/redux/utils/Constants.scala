package it.unibo.osmos.redux.utils

import java.nio.file.{FileSystem, FileSystems}

object Constants {

  object General {
    final val CellMaxSpeed: Double = 4
  }

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
    final val maxSpeed: Double = 2
    final val MaxAcceleration: Double = 0.1
    final val CoefficientDesiredSeparation: Double = 50
    final val MIN_VALUE: Double = 1
    final val PercentageOfLostRadiusForMagnitudeAcceleration: Double = 0.02
    final val MinRadiusForLostRadiusBehaviour: Double = 15
    final val WeightOfEscapeAccelerationFromEnemies: Double = 2
    final val WeightOfEscapeAccelerationFromBoundary: Double = WeightOfEscapeAccelerationFromEnemies * 1.5
  }

  object ResourcesPaths {
    val Separator: String = "/"
    val LevelStartPath: String = Separator + "levels"
    val SinglePlayerLevelsPath: String = LevelStartPath + Separator + "singlePlayer" + Separator
    val MultiPlayerLevelsPath: String = LevelStartPath + Separator + "multiPlayer" + Separator
    val ConfigSinglePlayer:String = LevelStartPath + Separator + "config" + Separator + "SinglePlayerLevels"
    val ConfigMultiPlayer:String = LevelStartPath + Separator + "config" + Separator + "MultiPlayerLevels"
    val SoundsPath: String = Separator + "sounds" + Separator
    val StylePath: String = Separator + "style" + Separator + "style.css"
  }

  object UserHomePaths {
    val DefaultFS: FileSystem = FileSystems.getDefault
    val SystemSeparator: String = DefaultFS.getSeparator
    val UserHome: String = System.getProperty("user.home")
    val GameDirectory:String = "Osmos-Redux" + SystemSeparator
    val LevelsDirectory: String = UserHome + SystemSeparator + GameDirectory + "CustomLevels" + SystemSeparator
    val UserProgressDirectory:String = UserHome + SystemSeparator + GameDirectory +
       "UserProgress" + SystemSeparator
    val UserProgressFileName: String = UserProgressDirectory + "UserProgress"
    val SettingFilePath:String = UserHome + SystemSeparator + GameDirectory + "Settings" + SystemSeparator + "GeneralSettings"
  }
}
