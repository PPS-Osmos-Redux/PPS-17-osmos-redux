package it.unibo.osmos.redux.mvc.model

import it.unibo.osmos.redux.mvc.controller.FileManager
import javafx.scene.media.MediaPlayer.Status
import javafx.scene.media.{Media, MediaPlayer}
object SoundsType extends Enumeration {
  val menu, level = Value
}

object MediaPlayer {
  private var mediaPlayer:Option[MediaPlayer] = None
  private var lastLoadedSound:Option[String] = None

  private def canApplyStateChange(allowedStates:List[Status]):Boolean = mediaPlayer match {
    case Some(mp) if allowedStates.contains(mp.getStatus) => true
    case _ => println("Error: cannot change media player status"); false
  }

  def createMediaPlayer(sound:String): Unit = {
    mediaPlayer = Some(new MediaPlayer(new Media(sound)))
    lastLoadedSound = Some(sound)
  }

  def checkMPExists(sound:String): Unit = {
    if(canApplyStateChange(List(Status.STOPPED,Status.PAUSED)) && lastLoadedSound.get.equals(sound)){
      mediaPlayer.get.play()
    } else if (mediaPlayer.isEmpty) {
      createMediaPlayer(sound)
      mediaPlayer.get.play()
    } else if (mediaPlayer.isDefined && !lastLoadedSound.get.equals(sound)) {
      mediaPlayer.get.stop()
      createMediaPlayer(sound)
      mediaPlayer.get.play()
    }
  }

  def play(sound:SoundsType.Value): Unit = sound match {
    case SoundsType.menu => checkMPExists(FileManager.loadMenuMusic())
    case SoundsType.level => checkMPExists(FileManager.loadLevelMusic())
    case _ => println("Sound type not managed!");
  }

  def stop(): Unit = if(canApplyStateChange(List(Status.PLAYING, Status.PAUSED))) mediaPlayer = None

  def pause(): Unit =  if(canApplyStateChange(List(Status.PLAYING))) mediaPlayer.get.pause()
}
