package github.com.dpratt747
package endpoints

import domain.*
import endpoints.*

import io.circe.*
import io.circe.generic.auto.*
import io.circe.syntax.*
import zhttp.*
import zhttp.http.*
import zhttp.service.*
import zhttp.service.ChannelEvent.*
import zhttp.socket.*
import zio.*

trait PostInterface {
  val routes: Http[Any, Throwable, Request, Response]
}
object Post extends PostInterface {

  val routes: Http[Any, Throwable, Request, Response] = Http.collectZIO[Request] {
    case req @ Method.POST -> !! / "user" =>
      for {
        createUser <- req.body.decodeJsonBody[User]
        res <- ZIO.attempt(Response.ok)
      } yield res
    case _ =>
      ZIO.succeed(Response.status(Status.NotFound))
  }

}