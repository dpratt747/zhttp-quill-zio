package github.com.dpratt747
package endpoints

import domain.*

import io.circe.*
import io.circe.generic.auto.*
import io.circe.syntax.*
import zhttp.*
import zhttp.http.*
import zhttp.service.*
import zhttp.service.ChannelEvent.*
import zhttp.socket.*
import zio.*
import endpoints.*

object Get {

  val routes: Http[Any, Throwable, Request, Response] = Http.collectZIO[Request] {
    case Method.GET -> !! / "example" => ZIO.attempt(Response.json(ZioHttpExampleJsonResponse("Hello World!").toJsonString))
    case _ => ZIO.succeed(Response.status(Status.NotFound))
  }

}