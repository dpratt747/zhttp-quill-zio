package github.com.dpratt747
package db.session

import config.*
import db.domain.UserTable
import domain.*

import com.typesafe.config.{Config, ConfigFactory, ConfigObject}
import io.getquill.*
import io.getquill.context.ZioJdbc.*
import io.getquill.jdbczio.Quill
import zio.*
import zio.config.magnolia

import java.sql.SQLException
import javax.sql.DataSource

trait PostgresContextAlg {
  def context: PostgresZioJdbcContext[SnakeCase.type]
}

final case class PostgresContext(
    private val dbConfig: DBConfig
) extends PostgresContextAlg {

  val context: PostgresZioJdbcContext[SnakeCase.type] =
    new PostgresZioJdbcContext(dbConfig.tableNameCase)

}

object PostgresContext {

  val live: ZLayer[ApplicationConfig, Nothing, DataSource & PostgresContext] = {
    import zio.config.*
    import ConfigDescriptor.*
    import zio.config.magnolia.*
    import zio.config.typesafe.*

    given portDescriptor: Descriptor[Port] = magnolia.Descriptor.from(
      descriptor[Int].transform[Port](Port(_), _.asInt)
    )

    given connectionTimeoutDescriptor: Descriptor[ConnectionTimeout] = magnolia.Descriptor.from(
      descriptor[Int].transform[ConnectionTimeout](ConnectionTimeout(_), _.asInt)
    )

    given userNameDescriptor: Descriptor[UserName] = magnolia.Descriptor.from(
      descriptor[String].transform[UserName](UserName(_), _.asString)
    )

    given passwordDescriptor: Descriptor[Password] = magnolia.Descriptor.from(
      descriptor[String].transform[Password](Password(_), _.asString)
    )

    given databaseDescriptor: Descriptor[Database] = magnolia.Descriptor.from(
      descriptor[String].transform[Database](Database(_), _.asString)
    )

    given serverNameDescriptor: Descriptor[ServerName] =
      magnolia.Descriptor.from(
        descriptor[String].transform[ServerName](ServerName(_), _.asString)
      )

    val postgresConfigDescriptor: ConfigDescriptor[DBConfig] = descriptor[DBConfig]

    def toTypeSafeConfig(
        config: DBConfig
    ): ZIO[Any, Throwable, Config] = for {
      configObject <- ZIO
        .fromEither(config.toHocon(postgresConfigDescriptor))
        .orElseFail(
          new RuntimeException("failed to create a typesafe config object")
        )
      newConfig <- ZIO.attempt(configObject.toConfig())
    } yield newConfig


    ZLayer.service[ApplicationConfig].flatMap{ env =>
      val pgConfig = env.get.postgres
      val typeSafeConfig = toTypeSafeConfig(pgConfig)

      val dataSourceLayer: ULayer[DataSource] = ZLayer.fromZIO(typeSafeConfig).flatMap(conf => Quill.DataSource.fromConfig(conf.get)).orDie
      val contextLayer: ULayer[PostgresContext] = ZLayer.succeed(PostgresContext(pgConfig))

      dataSourceLayer ++ contextLayer
    }
  }
}
