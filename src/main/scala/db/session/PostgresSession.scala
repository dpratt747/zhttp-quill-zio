package github.com.dpratt747
package db.session

import cats.effect.*
import cats.effect.kernel.Sync
import cats.effect.std.Console
import cats.syntax.all.*
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigObject
import github.com.dpratt747.config.ApplicationConfig
import github.com.dpratt747.config.DBConfig
import github.com.dpratt747.config.*
import github.com.dpratt747.domain.*
import io.getquill.*
import io.getquill.context.ZioJdbc.*
import io.getquill.jdbczio.Quill
import zio.*
import zio.interop.*
import zio.interop.catz.*

import java.sql.SQLException
import javax.sql.DataSource
import db.domain.UserTable
import domain.*

import zio.config.magnolia

trait PostgresContextAlg {
  def pgContext: PostgresZioJdbcContext[SnakeCase.type]
}

final case class PostgresContext(private val config: DBConfig)
    extends PostgresContextAlg {

  val pgContext: PostgresZioJdbcContext[SnakeCase.type] =
    new PostgresZioJdbcContext(config.tableNameCase)

}

object PostgresContext {
  val live: ZLayer[ApplicationConfig, Nothing, PostgresContextAlg] =
    ZLayer.fromZIO {
      for {
        config <- ZIO.serviceWith[ApplicationConfig](_.postgres)
      } yield PostgresContext(config)
    }
}

trait PostgresDataSourceAlg {
  def dataSource: ZLayer[Any, Throwable, DataSource]
}

final case class PostgresDataSource(private val config: Config)
    extends PostgresDataSourceAlg {

  def dataSource: ZLayer[Any, Throwable, DataSource] = Quill.DataSource.fromConfig(config)

}

object PostgresDataSource {

  val live: ZLayer[ApplicationConfig, Throwable, PostgresDataSourceAlg] = {
    import zio.config.typesafe.*
    import zio.config.*
    import zio.config.*, zio.config.typesafe.*
    import zio.config.magnolia.*
    import zio.config.*, ConfigDescriptor.*

    given portDescriptor: Descriptor[Port] = magnolia.Descriptor.from(descriptor[Int].transform[Port](Port(_), _.asInt))

    given userNameDescriptor: Descriptor[UserName] = magnolia.Descriptor.from(descriptor[String].transform[UserName](UserName(_), _.asString))

    given passwordDescriptor: Descriptor[Password] = magnolia.Descriptor.from(descriptor[String].transform[Password](Password(_), _.asString))

    given databaseDescriptor: Descriptor[Database] = magnolia.Descriptor.from(descriptor[String].transform[Database](Database(_), _.asString))

    given serverNameDescriptor: Descriptor[ServerName] = magnolia.Descriptor.from(descriptor[String].transform[ServerName](ServerName(_), _.asString))

    val descriptorDataSourceConfig: ConfigDescriptor[DataSourceConfig] = descriptor[DataSourceConfig]

    def toTypeSafeConfig(config: DataSourceConfig): ZIO[Any, Throwable, Config] = for {
      configObject <- ZIO
        .fromEither(config.toHocon(descriptorDataSourceConfig))
        .orElseFail(
          new RuntimeException("failed to create a typesafe config object")
        )
      newConfig <- ZIO.attempt(configObject.toConfig())
    } yield newConfig

    val configLayer: ZLayer[ApplicationConfig, Throwable, Config] =
      ZLayer.fromZIO(for {
        config: DBConfig <- ZIO.serviceWith[ApplicationConfig](_.postgres)
        toTpeSafe <- toTypeSafeConfig(config.dataSource)
      } yield toTpeSafe)

    configLayer.flatMap { (config: ZEnvironment[Config]) =>
      PostgresDataSource(config.get)
    }
  }

}
