package it.unibo.osmos.redux.mvc.model

import it.unibo.osmos.redux.mvc.controller.manager.files.SettingsFileManger
import it.unibo.osmos.redux.mvc.controller.manager.sounds.MusicPlayer
import it.unibo.osmos.redux.utils.Logger

object SettingsHolder extends SettingsEventObserver {
  implicit val who:String = "Settings"

  var settings:List[Setting] = List()

  def init(loadedSettings: List[Setting]): Unit = loadedSettings.foreach(setting => sendSettingToDestination(setting))

  private def sendSettingToDestination(setting:Setting): Unit = setting match {
    case vol:Volume =>if(vol.isMute) MusicPlayer.pause()
                      MusicPlayer.changeVolume(vol.value)
    case _ => Logger.log("Error setting not managed " + setting)
  }

  override def notify(settingsEvent: SettingsEvent): Unit = settingsEvent match {
    case mpEvent: MusicPlayerEvent =>
      createOrUpdateSetting(mpEvent.volume, mpEvent.volume.settingType)
      SettingsFileManger.saveSettings(settings)
    case _ => Logger.log("Error settings event not managed " + settingsEvent)
  }

  private def createOrUpdateSetting(setting:Setting, settingType:SettingsTypes.Value): Unit =
    if(settings.map(_.settingType).contains(settingType)) {
      settings = settings.updated(indexOf(settingType), setting)
    } else {
      settings = setting :: settings
    }

  private def indexOf(settingType:SettingsTypes.Value):Int =  settings.map(st => st.settingType).indexOf(settingType)
}


/**
  * Trait extended by settings events
  */
sealed trait SettingsEvent

/**
  * Music player event
  * @param volume Double
  */
case class MusicPlayerEvent(volume:Volume) extends SettingsEvent

/**
  * Trait extends by a settings event generator
  */
trait Observable {
  def subscribe(observer:SettingsEventObserver)
}
/**
  * Trait extended by settings event handler
  */
trait SettingsEventObserver {
  /**
    * Setting event
    * @param settingsEvent SettingsEvent
    */
  def notify(settingsEvent:SettingsEvent)
}

object SettingsTypes extends Enumeration {
  val Volume: SettingsTypes.Value = Value
}


/**
  * Trait extended by setting
  */
sealed trait Setting {
  /*Setting name*/
  val settingType:SettingsTypes.Value
}

/**
  * Volume setting
  * @param value volume values
  */
case class Volume(var value:Double, val isMute:Boolean) extends Setting {
  override val settingType: SettingsTypes.Value = SettingsTypes.Volume
}
