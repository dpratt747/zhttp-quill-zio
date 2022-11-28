package github.com.dpratt747
package config

import config.ApplicationConfig
import domain.*

import io.getquill.SnakeCase
import zio.config.*
import zio.config.derivation.name
import zio.config.magnolia.{Descriptor, descriptor}
import zio.config.typesafe.*
import zio.*

final case class ServerConfig(port: Port)

final case class DataSourceConfig(
  portNumber: Port,
  user: UserName,
  password: Password,
  databaseName: Database,
  serverName: ServerName
)

final case class DBConfig(
                           dataSource: DataSourceConfig,
                           connectionTimeout: ConnectionTimeout,
                           dataSourceClassName: String
                         )
{
  val tableNameCase: SnakeCase.type = SnakeCase
  import dataSource._
  val connectionString = s"jdbc:postgresql://$serverName:$portNumber/$databaseName"
}

final case class ApplicationConfig(server: ServerConfig, postgres: DBConfig)

given portDescriptor: Descriptor[Port] = Descriptor.from(descriptor[Int].map(Port(_)))
given usernameDescriptor: Descriptor[UserName] = Descriptor.from(descriptor[String].map(UserName(_)))
given serverNameDescriptor: Descriptor[ServerName] = Descriptor.from(descriptor[String].map(ServerName(_)))
given passwordDescriptor: Descriptor[Password] = Descriptor.from(descriptor[String].map(Password(_)))
given databaseDescriptor: Descriptor[Database] = Descriptor.from(descriptor[String].map(Database(_)))
given hostDescriptor: Descriptor[Host] = Descriptor.from(descriptor[String].map(Host(_)))
given connectionTimeoutDescriptor: Descriptor[ConnectionTimeout] = Descriptor.from(descriptor[Int].map(ConnectionTimeout(_)))

val applicationConfigDescriptor: ConfigDescriptor[ApplicationConfig] = descriptor[ApplicationConfig]
val configLayer: Layer[ReadError[String], ApplicationConfig] = TypesafeConfig.fromResourcePath(applicationConfigDescriptor)