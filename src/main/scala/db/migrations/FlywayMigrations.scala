package github.com.dpratt747
package db.migrations

import org.flywaydb.core.{Flyway, *}
import zio.*
import domain.*

import github.com.dpratt747.config.ApplicationConfig
import zio.config.*
import github.com.dpratt747.config.DBConfig

trait FlywayMigrationsAlg {
  def migrate: ZIO[ApplicationConfig, Throwable, Unit]
  def clean: ZIO[ApplicationConfig, Throwable, Unit]
}

final case class FlywayMigrations(private val config: DBConfig)
    extends FlywayMigrationsAlg {

  private val flywayIO: Task[Flyway] = ZIO.attempt(
    Flyway
      .configure()
      .cleanDisabled(false)
      .dataSource(
        config.connectionString,
        config.dataSource.user.asString,
        config.dataSource.password.asString
      )
      .load()
  )

  override def migrate: ZIO[ApplicationConfig, Throwable, Unit] =
    for {
      flyway <- flywayIO
      _ <- ZIO.attempt(flyway.migrate())
    } yield ()

  override def clean: ZIO[ApplicationConfig, Throwable, Unit] =
    for {
      flyway <- flywayIO
      _ <- ZIO.attempt(flyway.clean())
    } yield ()

}

object FlywayMigrations {

  val live: ZLayer[ApplicationConfig, Nothing, FlywayMigrations] =
    ZLayer.fromZIO(
      for {
        config: DBConfig <- ZIO.serviceWith[ApplicationConfig](_.postgres)
      } yield FlywayMigrations(config)
    )

}
