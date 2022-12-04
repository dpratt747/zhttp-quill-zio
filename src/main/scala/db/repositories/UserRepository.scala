package github.com.dpratt747
package db.repositories

import config.ApplicationConfig
import db.domain.*
import db.session.*
import domain.*
import endpoints.*

import io.circe.generic.auto.*
import io.getquill.*
import zio.*

import java.sql.SQLException
import javax.sql.DataSource
import io.getquill.Query

trait UserRepositoryAlg {
  def insertUser(user: User, salt: Salt): ZIO[Any, Throwable, Unit]
}

final case class UserRepository(
    private val pgContext: PostgresZioJdbcContext[SnakeCase.type],
    private val dataSource: DataSource
) extends UserRepositoryAlg {

  import pgContext.*

  def insertUser(
      user: User,
      salt: Salt
  ): ZIO[Any, Throwable, Unit] = {

    val insertUserRowQuote: Quoted[ActionReturning[UserTable, Int]] = quote {
      query[UserTable]
        .insert(
          _.userName -> lift(user.userName.asString),
          _.firstName -> lift(user.firstName.asString),
          _.lastName -> lift(user.lastName.asString),
          _.passwordHash -> lift(user.password.asString)
        )
        .returning(_.id)
    }

    def insertSaltRowQuote(userId: Int) = quote {
      query[HashSaltTable].insert(
        _.userId -> lift(userId.asInt),
        _.salt -> lift(salt.asString)
      )
    }

    pgContext
      .transaction {
        for {
          userId: Int <- pgContext.run(insertUserRowQuote)
          _ <- pgContext.run(insertSaltRowQuote(userId))
        } yield ()
      }
      .provide(ZLayer.succeed(dataSource))
  }
}

object UserRepository {
  val live
      : ZLayer[DataSource with PostgresContextAlg, Nothing, UserRepository] =
    ZLayer.fromZIO {
      for {
        context <- ZIO.serviceWith[PostgresContextAlg](_.context)
        datasource <- ZIO.service[DataSource]
      } yield UserRepository(context, datasource)
    }
}
