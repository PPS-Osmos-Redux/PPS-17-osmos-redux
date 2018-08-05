package it.unibo.osmos.redux.main

import java.awt.BorderLayout

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.layout.BorderPane
import scalafx.scene.paint.Color._
import scalafx.scene.shape.Rectangle

object ScalaFxHello extends JFXApp {

  stage = new JFXApp.PrimaryStage {
    title.value = "Hello Stage"
    width = 600
    height = 450
    scene = new Scene() {
      val layout = new BorderPane
      root = layout

      fill = LightGreen
      content = new Rectangle {
        x = 50
        y = 50
        width = 100
        height = 100
        fill <== when(hover) choose Green otherwise Red
      }
    }
  }

  val scene1 = new Scene {
    fill = Blue
  }

  val scene2 = new Scene {
    fill = Green
  }

}
