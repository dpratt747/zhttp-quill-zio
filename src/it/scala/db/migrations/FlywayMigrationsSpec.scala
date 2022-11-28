package github.com.dpratt747
package db.migrations

import config.{ApplicationConfig, configLayer}
import domain.*

import org.flywaydb.core.api.output.CleanResult
import org.flywaydb.core.*
import zio.config.typesafe.TypesafeConfigSource
import zio.config.{ConfigSource, ReadError, read}
import zio.test.*
import zio.test.Assertion.*
import zio.{Scope, ZIO}

import scala.jdk.CollectionConverters.*

object FlywayMigrationsSpec extends ZIOSpecDefault {
  override def spec =
    suite("FlywayMigrations")(
      test("can connect and run the migrations and clean") {
        (for {
          _ <- ZIO.serviceWithZIO[FlywayMigrationsAlg](_.migrate)
          config <- ZIO.serviceWith[ApplicationConfig](_.postgres)
          flyway <- ZIO.attempt(
            Flyway
              .configure()
              .cleanDisabled(false)
              .dataSource(config.connectionString, config.dataSource.user.asString, config.dataSource.password.asString)
              .load()
          )
          appliedMigrations <- ZIO.attempt(flyway.info().applied().length)
          cleanRes <- ZIO.attempt(flyway.clean().schemasCleaned.size())
        } yield assertTrue(appliedMigrations == 1 && cleanRes == appliedMigrations))
          .provide(FlywayMigrations.live, configLayer)
      }
    )
}