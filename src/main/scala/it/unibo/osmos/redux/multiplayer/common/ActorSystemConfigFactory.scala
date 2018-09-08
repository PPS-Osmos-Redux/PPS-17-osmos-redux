package it.unibo.osmos.redux.multiplayer.common

import com.typesafe.config.{Config, ConfigFactory}
import it.unibo.osmos.redux.utils.Constants

import scala.io.Source

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
    * @return The default config with custom port and address.
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
    * Loads and returns the config declared in the default.conf file in the resources.
    * @return The config.
    */
  def load(): Config = {
    ConfigFactory.parseString(readConfigFile)
  }

  /**
    * Reads from the default system conf file.
    * @return The file content.
    */
  private def readConfigFile: String = {
    val fileStream = getClass.getClassLoader.getResourceAsStream(Constants.MultiPlayer.defaultSystemConfig)
    try Source.fromInputStream(fileStream).mkString
    finally fileStream.close()
  }
}
