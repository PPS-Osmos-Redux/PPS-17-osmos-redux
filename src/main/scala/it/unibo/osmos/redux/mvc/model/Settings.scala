package it.unibo.osmos.redux.mvc.model

import it.unibo.osmos.redux.mvc.controller.manager.files.SettingsFileManger
import it.unibo.osmos.redux.mvc.controller.manager.sounds.MusicPlayer
import it.unibo.osmos.redux.utils.Logger

/**Manages settings*/
object SettingsHolder extends SettingsEventObserver {
  implicit val who:String = "Settings"

  var settings:List[Setting] = List()

  /** Initialize settings list
    *
    * @param loadedSettings List[Setting]
    */
  def init(loadedSettings: List[Setting]): Unit = loadedSettings.foreach(setting => sendSettingToDestination(setting))

  override def notify(settingsEvent: SettingsEvent): Unit = settingsEvent match {
    case mpEvent: MusicPlayerEvent =>
      createOrUpdateSetting(mpEvent.volume, mpEvent.volume.settingType)
      SettingsFileManger.saveSettings(settings)
    case _ => Logger.log("Error settings event not managed " + settingsEvent)
  }

  private def sendSettingToDestination(setting:Setting): Unit = setting match {
    case vol:Volume => MusicPlayer.changeVolume(vol.value)
    case _ => Logger.log("Error setting not managed " + setting)
  }

  private def createOrUpdateSetting(setting:Setting, settingType:SettingsTypes.Value): Unit =
    if(settings.map(_.settingType).contains(settingType)) {
      settings = settings.updated(indexOf(settingType), setting)
    } else {
      settings = setting :: settings
    }

  private def indexOf(settingType:SettingsTypes.Value):Int =  settings.map(st => st.settingType).indexOf(settingType)
}


/**Trait extended by settings events*/
sealed trait SettingsEvent

/** Music player event
  *
  * @param volume Double
  */
case class MusicPlayerEvent(volume:Volume) extends SettingsEvent

/**Trait extends by a settings event generator*/
trait Observable {
  def subscribe(observer:SettingsEventObserver)
}
/**Trait extended by settings event handler*/
trait SettingsEventObserver {
  /** Setting event
    *
    * @param settingsEvent SettingsEvent
    */
  def notify(settingsEvent:SettingsEvent)
}

/**Defines settings types*/
object SettingsTypes extends Enumeration {
  val Volume: SettingsTypes.Value = Value
}


/**Trait extended by setting*/
sealed trait Setting {
  /* Setting type */
  val settingType:SettingsTypes.Value
}

/**Defines volume setting
  *
  * @param value volume value
  */
case class Volume(var value:Double) extends Setting {
  override val settingType: SettingsTypes.Value = SettingsTypes.Volume
}
