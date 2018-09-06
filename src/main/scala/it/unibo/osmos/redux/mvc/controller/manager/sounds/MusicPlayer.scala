package it.unibo.osmos.redux.mvc.controller.manager.sounds

import it.unibo.osmos.redux.mvc.controller.Controller
import it.unibo.osmos.redux.mvc.controller.manager.files.{FileManager, SoundFileManager}
import it.unibo.osmos.redux.mvc.model._
import javafx.scene.media.MediaPlayer.Status
import javafx.scene.media.MediaPlayer.Status._
import javafx.util

/**
  * Sound types
  */
object SoundsType extends Enumeration {
  val menu, level, button = Value
}

object MusicPlayer extends Observable {
  import scalafx.scene.media.{AudioClip, Media, MediaPlayer}
  val MinVolume = 0
  val MaxVolume = 1
  private var controller: Option[Controller] = None
  private var mediaPlayer: Option[MediaPlayer] = None
  private var lastLoadedSound: Option[String] = None
  private var buttonAudioClip: Option[AudioClip] = None
  private var generalVolume: Double = MaxVolume
  private var settingObs:List[SettingsEventObserver] = List(SettingsHolder)

  def setController(controller: Controller): Unit = this.controller = Some(controller)

  /**
    * Play a sound
    *
    * @param sound the sound to play
    */
  def play(sound: SoundsType.Value): Unit = controller match {
    case Some(_) =>
      getMediaPlayerStatus match {
        case Some(PAUSED) =>
        case _ =>
          val soundPath = controller.get.getSoundPath(sound)
          if (soundPath.isDefined) {
            sound match {
              case s if s.equals(SoundsType.level) || s.equals(SoundsType.menu) => checkMediaPlayerStatus(soundPath.get)
              case SoundsType.button => playButtonSound(soundPath.get)
              case _ => println("Sound not managed! [MediaPlayer play]")
            }
          }
      }
    case _ => println("Error: controller is not defined [MediaPlayer play]")
  }

  /**
    * Pause the music
    */
  def pause(): Unit = if (canApplyStateChange(List(Status.PLAYING))) {mediaPlayer.get.pause(); sendMusicEvent(true)}

  /**
    * Resume the music if it is in pause state
    */
  def resume(): Unit = if (canApplyStateChange(List(Status.PAUSED))) mediaPlayer.get.play()

  /**
    * Change music and audio effects volume
    *
    * @param volume double value for volume, range 0 to 1
    */
  def changeVolume(volume: Double): Unit = {
    volume match {
      case v if v <= MinVolume => generalVolume = MinVolume
      case v if v >= MaxVolume => generalVolume = MaxVolume
      case _ => generalVolume = volume
    }
    sendMusicEvent()
    updateMPVolume()
  }

  /**
    * Return current application volume
    *
    * @return application volume
    */
  def getVolume: Double = generalVolume

  /**
    * Return current media player status
    *
    * @return Option of MediaPlayer.Status
    */
  def getMediaPlayerStatus: Option[Status] = mediaPlayer match {
    case Some(mp) => Option(mp.getStatus)
    case _ => None
  }

  private def updateMPVolume(): Unit = if (mediaPlayer.isDefined) mediaPlayer.get.setVolume(generalVolume)

  private def playButtonSound(sound: String): Unit = buttonAudioClip match {
    case Some(bac) => bac.play(generalVolume)
    case _ => buttonAudioClip = Some(new AudioClip(SoundFileManager.loadButtonsSound())); playButtonSound(sound)
  }

  private def canApplyStateChange(allowedStates: List[Status]): Boolean = mediaPlayer match {
    case Some(mp) if allowedStates.contains(mp.getStatus) => true
    case _ => false
  }

  private def setupAndPlayMedia(sound: String): Unit = {
    if (mediaPlayer.isDefined) mediaPlayer.get.stop()
    mediaPlayer = Some(new MediaPlayer(new Media(sound)))
    mediaPlayer.get.setOnEndOfMedia(() => mediaPlayer.get.seek(util.Duration.ZERO))
    lastLoadedSound = Some(sound)
    updateMPVolume()
    mediaPlayer.get.play()
  }

  private def checkMediaPlayerStatus(sound: String): Unit = {
    if (canApplyStateChange(List(Status.STOPPED, Status.PAUSED)) && lastLoadedSound.get.equals(sound)) {
      mediaPlayer.get.play()
    } else {
      setupAndPlayMedia(sound)
    }
  }

  override def subscribe(observer:SettingsEventObserver): Unit = settingObs = observer :: settingObs

  private def sendMusicEvent(isMute:Boolean = false): Unit = {
    settingObs.foreach(_.notify(MusicPlayerEvent(Volume(if(isMute) MinVolume else generalVolume))))
  }
}
