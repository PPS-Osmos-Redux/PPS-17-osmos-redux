package it.unibo.osmos.redux.mvc.model

import it.unibo.osmos.redux.mvc.controller.{Controller, FileManager}
import javafx.scene.media.MediaPlayer.Status
import javafx.scene.media.{Media, MediaPlayer}
import javafx.util
import scalafx.scene.media.AudioClip

/**
  * Sound types
  */
object SoundsType extends Enumeration {
  val menu, level, button = Value
}

object MediaPlayer {
  private var controller:Option[Controller] = None
  private var mediaPlayer:Option[MediaPlayer] = None
  private var lastLoadedSound:Option[String] = None
  private var buttonAudioClip:Option[AudioClip] = None
  private var generalVolume:Double = 1


  def setController(controller: Controller): Unit = this.controller = Some(controller)

  /**
    * Play a sound
    * @param sound the sound to play
    */
  def play(sound:SoundsType.Value): Unit = {
    val soundPath = controller.get.getSoundPath(sound)
    if(soundPath.isDefined) {
      sound match {
        case s if s.equals(SoundsType.level) || s.equals(SoundsType.menu) => checkMediaPlayerStatus(soundPath.get)
        case SoundsType.button => playButtonSound(soundPath.get)
        case _ => println("Sound not managed! [MediaPlayer play]")
      }
    }
  }

  /**
    * Pause the music
    */
  def pause(): Unit =  if(canApplyStateChange(List(Status.PLAYING))) mediaPlayer.get.pause()

  /**
    * Resume the music if it is in pause state
    */
  def resume(): Unit = if(canApplyStateChange(List(Status.PAUSED))) mediaPlayer.get.play()

  /**
    * Change music and audio effects volume
    * @param volume double value for volume, range 0 to 1
    */
  def changeVolume(volume:Double): Unit = {
    volume match {
      case v if v <= 0 => generalVolume = 0
      case v if v >= 1 => generalVolume = 1
      case _ => generalVolume = volume
    }
    updateMPVolume()
  }

  private def updateMPVolume(): Unit = if(mediaPlayer.isDefined) mediaPlayer.get.setVolume(generalVolume)

  private def playButtonSound(sound:String): Unit = buttonAudioClip match {
    case Some(bac) => bac.play(generalVolume)
    case _ => buttonAudioClip = Some(new AudioClip(FileManager.loadButtonsSound())); playButtonSound(sound)
  }

  private def canApplyStateChange(allowedStates:List[Status]):Boolean = mediaPlayer match {
    case Some(mp) if allowedStates.contains(mp.getStatus) => true
    case _ => false
  }

  private def setupAndPlayMedia(sound:String): Unit = {
    if(mediaPlayer.isDefined) mediaPlayer.get.stop()
    mediaPlayer = Some(new MediaPlayer(new Media(sound)))
    mediaPlayer.get.setOnEndOfMedia(() => mediaPlayer.get.seek(util.Duration.ZERO))
    lastLoadedSound = Some(sound)
    updateMPVolume()
    mediaPlayer.get.play()
  }

  private def checkMediaPlayerStatus(sound:String): Unit = {
    if(canApplyStateChange(List(Status.STOPPED,Status.PAUSED)) && lastLoadedSound.get.equals(sound)){
      mediaPlayer.get.play()
    } else {
      setupAndPlayMedia(sound)
    }
  }
}
