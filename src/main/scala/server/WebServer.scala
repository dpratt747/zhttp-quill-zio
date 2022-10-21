package github.com.dpratt747
package server

import config.{ApplicationConfig, configLayer}

import endpoints.*
import zhttp.http.*
import zhttp.service.*
import zio.*
import zhttp.service

object WebServer extends ZIOAppDefault {

  override def run: ZIO[Any, Throwable, Unit] =
    (for {
      env <- ZIO.environment[ApplicationConfig]
      config = env.get[ApplicationConfig]
      _ <- Server.start(config.server.port, Get.routes)
    } yield ())
      .provideLayer(configLayer)

}