package github.com.dpratt747
package server

import config.{ApplicationConfig, configLayer}
import domain.*
import endpoints.*

import github.com.dpratt747.db.repositories.UserRepository
import github.com.dpratt747.db.session.PostgresContext
import github.com.dpratt747.hash.PasswordHashing
import github.com.dpratt747.programs.CreateUserProgram
import zhttp.http.*
import zhttp.service
import zhttp.service.*
import zio.*

object WebServer extends ZIOAppDefault {

  override def run: ZIO[Any, Throwable, Unit] =
    (for {
      port <- ZIO.serviceWith[ApplicationConfig](_.server.port)
      postR <- ZIO.serviceWith[PostAlg](_.routes)
      getR <- ZIO.serviceWith[GetAlg](_.routes)
      _ <- Server.start(port.asInt, getR ++ postR)
    } yield ())
      .provide(
        configLayer,
        Get.live,
        Post.live,
        PostgresContext.live,
        CreateUserProgram.live,
        UserRepository.live,
        PasswordHashing.live
      )

}