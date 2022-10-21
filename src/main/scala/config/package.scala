package github.com.dpratt747
package config

import config.ApplicationConfig

import zio.config.*
import zio.config.derivation.name
import zio.config.magnolia.{Descriptor, descriptor}
import zio.config.typesafe.*
import zio.*

final case class ServerConfig(port: Int)
final case class ApplicationConfig(server: ServerConfig)

val configDescriptor: ConfigDescriptor[ApplicationConfig] = descriptor[ApplicationConfig]
val configLayer: Layer[ReadError[String], ApplicationConfig] = TypesafeConfig.fromResourcePath(configDescriptor)
