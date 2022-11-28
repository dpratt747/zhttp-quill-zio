package github.com.dpratt747
package db.session

import cats.effect.implicits.*
import github.com.dpratt747.db.domain.HashSaltTable
import github.com.dpratt747.db.domain.UserTable
import github.com.dpratt747.db.migrations.FlywayMigrations
import github.com.dpratt747.db.migrations.*
import org.flywaydb.core.*
import org.flywaydb.core.api.output.CleanResult
import zio.Scope
import zio.Task
import zio.ZIO
import zio.config.ConfigSource
import zio.config.ReadError
import zio.config.read
import zio.config.typesafe.TypesafeConfigSource
import zio.interop.*
import zio.interop.catz.*
import zio.test.Assertion.*
import zio.test.*

import scala.jdk.CollectionConverters.*

import config.{ApplicationConfig, configLayer}
import domain.*
import javax.sql.DataSource
import io.getquill.jdbczio.Quill
import io.getquill.*
import io.getquill.context.ZioJdbc.*
import java.sql.SQLException

object PostgresSessionSpec extends ZIOSpecDefault {
  override def spec =
    suite("PostgresSessionSpec")(
      test(
        "can create a session and transaction that allows for insertion"
      ) {
        checkN(30)(Gen.string, Gen.string, Gen.string, Gen.string) { (userName, firstName, lastName, passwordHash) =>

            def action(
                pgContext: PostgresZioJdbcContext[SnakeCase.type]
            ): ZIO[DataSource, SQLException, Long] = {
              import pgContext.*

              val insertUserRowQuote: Quoted[Insert[UserTable]] = quote {
                query[UserTable].insert(
                  _.userName -> lift(userName),
                  _.firstName -> lift(firstName),
                  _.lastName -> lift(lastName),
                  _.passwordHash -> lift(passwordHash)
                )
              }

              // pgContext.transaction{
                pgContext.run[UserTable](insertUserRowQuote)
              // }     
            }

            (for {
              _ <- ZIO.serviceWithZIO[FlywayMigrationsAlg](_.migrate)
              config <- ZIO.serviceWith[ApplicationConfig](_.postgres)
              context <- ZIO.serviceWith[PostgresContextAlg](_.pgContext)
              insertResult <- action(context)
            } yield assertTrue(insertResult == 1L))
              .provide(
                PostgresDataSource.live,
                PostgresContext.live,
                FlywayMigrations.live,
                configLayer
              )
        }
      }
    ) @@ TestAspect.afterAll(ZIO.serviceWithZIO[FlywayMigrationsAlg](_.clean).provide(FlywayMigrations.live, configLayer).ignore) @@
     TestAspect.beforeAll(ZIO.serviceWithZIO[FlywayMigrationsAlg](_.migrate).provide(FlywayMigrations.live, configLayer))
}
