package it.unibo.osmos.redux.mvc.model

import it.unibo.osmos.redux.mvc.controller.FileManager
import javafx.scene.media.MediaPlayer.Status
import javafx.scene.media.{Media, MediaPlayer}
import javafx.util
import scalafx.scene.media.AudioClip

object SoundsType extends Enumeration {
  val menu, level, button = Value
}

object MediaPlayer {
  private var mediaPlayer:Option[MediaPlayer] = None
  private var lastLoadedSound:Option[String] = None
  private var buttonAudioClip:Option[AudioClip] = None
  private var volume:Double = 100

  private def canApplyStateChange(allowedStates:List[Status]):Boolean = mediaPlayer match {
    case Some(mp) if allowedStates.contains(mp.getStatus) => true
    case _ => println("Error: cannot change media player status"); false
  }

  private def setupAndPlayMedia(sound:String): Unit = {
    if(mediaPlayer.isDefined) mediaPlayer.get.stop()
    mediaPlayer = Some(new MediaPlayer(new Media(sound)))
    mediaPlayer.get.setOnEndOfMedia(() => mediaPlayer.get.seek(util.Duration.ZERO))
    lastLoadedSound = Some(sound)
    mediaPlayer.get.play()
  }

  private def checkMediaPlayerStatus(sound:String): Unit = {
    if(canApplyStateChange(List(Status.STOPPED,Status.PAUSED)) && lastLoadedSound.get.equals(sound)){
      mediaPlayer.get.play()
    } else {
      setupAndPlayMedia(sound)
    }
  }

  def play(sound:SoundsType.Value): Unit = sound match {
    case SoundsType.menu => checkMediaPlayerStatus(FileManager.loadMenuMusic())
    case SoundsType.level => checkMediaPlayerStatus(FileManager.loadLevelMusic())
    case SoundsType.button => playButtonSound(FileManager.loadButtonsSound())
    case _ => println("Sound type not managed!");
  }

  def stop(): Unit = if(canApplyStateChange(List(Status.PLAYING, Status.PAUSED))) mediaPlayer = None

  def pause(): Unit =  if(canApplyStateChange(List(Status.PLAYING))) mediaPlayer.get.pause()

  private def playButtonSound(sound:String): Unit = buttonAudioClip match {
    case Some(bac) => bac.play(volume)
    case _ => buttonAudioClip = Some(new AudioClip(FileManager.loadButtonsSound())); playButtonSound(sound)
  }
}
