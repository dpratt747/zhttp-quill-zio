package github.com.dpratt747
package server

import config.{ApplicationConfig, configLayer}
import domain.*
import endpoints.*

import zhttp.http.*
import zhttp.service
import zhttp.service.*
import zio.*

object WebServer extends ZIOAppDefault {

  override def run: ZIO[Any, Throwable, Unit] =
    (for {
      env <- ZIO.environment[ApplicationConfig]
      config = env.get[ApplicationConfig]
      port = config.server.port.asInt
      _ <- Server.start(port, Get.routes)
    } yield ())
      .provideLayer(configLayer)

}