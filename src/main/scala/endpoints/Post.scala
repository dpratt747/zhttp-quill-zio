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

object Post {

  val routes: Http[Any, Throwable, Request, Response] = Http.collectZIO[Request] {
    case req @ Method.POST -> !! / "user" =>
      ZIO.attempt(println(req.body)) *> ZIO.attempt(Response.ok)
    case _ => ZIO.succeed(Response.status(Status.NotFound))
  }

}