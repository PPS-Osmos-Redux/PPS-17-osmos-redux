package it.unibo.osmos.redux

import it.unibo.osmos.redux.mvc.controller.ControllerImpl
import it.unibo.osmos.redux.mvc.model.{MediaPlayer, SoundsType}
import org.scalatest.FunSuite

class TestMediaPlayer extends FunSuite {
  val sleepTime = 1000
  val maxVol = 10
  val minVol = 0
  //init javafx toolkit
  com.sun.javafx.application.PlatformImpl.startup(() => {
    def foo(): Unit = {}
    foo()
  })

  test("Control media player") {
    MediaPlayer.setController(ControllerImpl())
    assert(MediaPlayer.getMediaPlayerStatus.isEmpty)
    MediaPlayer.pause()
    assert(MediaPlayer.getMediaPlayerStatus.isEmpty)

    MediaPlayer.play(SoundsType.level)
    assert(MediaPlayer.getMediaPlayerStatus.isDefined)
    Thread.sleep(sleepTime)
    assert(MediaPlayer.getMediaPlayerStatus.get.equals(javafx.scene.media.MediaPlayer.Status.PLAYING))

    MediaPlayer.pause()
    Thread.sleep(sleepTime)
    assert(MediaPlayer.getMediaPlayerStatus.get.equals(javafx.scene.media.MediaPlayer.Status.PAUSED))

    MediaPlayer.resume()
    Thread.sleep(sleepTime)
    assert(MediaPlayer.getMediaPlayerStatus.get.equals(javafx.scene.media.MediaPlayer.Status.PLAYING))

    MediaPlayer.play(SoundsType.menu)
    Thread.sleep(sleepTime)
    assert(MediaPlayer.getMediaPlayerStatus.get.equals(javafx.scene.media.MediaPlayer.Status.PLAYING))

    MediaPlayer.play(SoundsType.button)
  }

  test("Change volume") {
    MediaPlayer.play(SoundsType.menu)
    Thread.sleep(sleepTime)
    MediaPlayer.changeVolume(100)
    assert(MediaPlayer.getVolume == 1)
    MediaPlayer.changeVolume(-1)
    assert(MediaPlayer.getVolume == 0)
    Range(minVol,maxVol,1).map(i => i/10).foreach(vol => {
      MediaPlayer.changeVolume(vol)
      assert(MediaPlayer.getVolume == vol)
    })
  }
}
