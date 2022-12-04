package github.com.dpratt747
package endpoints

import domain.*
import endpoints.*

import github.com.dpratt747.config.configLayer
import github.com.dpratt747.db.session.PostgresContext
import github.com.dpratt747.hash.PasswordHashing
import github.com.dpratt747.programs.{CreateUserProgram, CreateUserProgramAlg}
import io.circe.*
import io.circe.generic.auto.*
import io.circe.syntax.*
import zhttp.*
import zhttp.http.*
import zhttp.http.Status.BadGateway
import zhttp.service.*
import zhttp.service.ChannelEvent.*
import zhttp.socket.*
import zio.*

import javax.sql.DataSource

trait PostAlg {
  val routes: Http[Any, Throwable, Request, Response]
}
final case class Post(
    private val createUserProgram: CreateUserProgramAlg,
    private val dataSource: DataSource
) extends PostAlg {

  val routes: Http[Any, Throwable, Request, Response] =
    Http.collectZIO[Request] {
      case req @ Method.POST -> !! / "user" =>
        (for {
          user <- req.body.decodeJsonBody[User]
          res <- createUserProgram.createUser(user).as(Response.ok)
        } yield res)
          .catchSome {
            case _: DecodingFailure =>
              ZIO.attempt(Response.status(Status.BadRequest))
            case _ =>
              ZIO.attempt(Response.status(Status.BadGateway))
          }
      case _ =>
        ZIO.succeed(Response.status(Status.NotFound))
    }
}

object Post {
  val live: ZLayer[DataSource with CreateUserProgramAlg, Nothing, Post] =
    ZLayer.fromZIO {
      for {
        cuAlg <- ZIO.service[CreateUserProgramAlg]
        dataSource <- ZIO.service[DataSource]
      } yield Post(cuAlg, dataSource)
    }
}
