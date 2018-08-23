package it.unibo.osmos.redux.multiplayer.common

import java.io.File

import com.typesafe.config.{Config, ConfigFactory}
import it.unibo.osmos.redux.utils.Constants

object ActorSystemConfigFactory {

  def create(): Config = {
    val configString = """
     akka {
       actor {
         provider = remote
       }
       remote {
         enabled-transports = ["akka.remote.netty.tcp"]
         netty.tcp {
           port = 0
         }
         log-sent-messages = on
         log-received-messages = on
       }
     }
      """
    ConfigFactory.parseString(configString)
  }

  def load(): Config = {
    val configFile = getClass.getClassLoader.getResource(Constants.defaultSystemConfig).getFile
    ConfigFactory.parseFile(new File(configFile))
  }
}
