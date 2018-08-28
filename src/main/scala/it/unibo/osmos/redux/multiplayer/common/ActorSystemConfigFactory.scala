package it.unibo.osmos.redux.multiplayer.common

import java.io.File

import com.typesafe.config.{Config, ConfigFactory}
import it.unibo.osmos.redux.utils.Constants

object ActorSystemConfigFactory {

  /**
    * Creates and returns a default config.
    * @return The default config.
    */
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
       }
     }
      """
    ConfigFactory.parseString(configString)
  }

  /**
    * Creates and returns a config with the specified address and port.
    * @param address The user object that contains declared port
    * @return
    */
  def create(address: String, port: String): Config = {
    val configString = s"""
     akka {
       actor {
         provider = remote
       }
       remote {
         enabled-transports = ["akka.remote.netty.tcp"]
         netty.tcp {
           address = $address
           port = $port
         }
       }
     }
      """
    ConfigFactory.parseString(configString)
  }

  /**
    * Loads and returns the config declared in the application.conf file in the resources.
    * @return
    */
  def load(): Config = {
    val configFile = getClass.getClassLoader.getResource(Constants.defaultSystemConfig).getFile
    ConfigFactory.parseFile(new File(configFile))
  }
}
