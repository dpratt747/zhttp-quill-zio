package github.com.dpratt747
package config

import config.*
import helper.*

import zio.*
import zio.config.typesafe.TypesafeConfigSource
import zio.config.{ConfigSource, ReadError, read}
import zio.test.*
import zio.test.Assertion.*

object ConfigSpec extends ZIOSpecDefault {
  override def spec: Spec[Any, ReadError[String]] =
    suite("ConfigSpec")(
      test("can load successfully") {
        (for {
          env <- ZIO.environment[ApplicationConfig]
          appConfig = env.get[ApplicationConfig]
        } yield assertTrue(appConfig.server.port == 8080))
          .provideLayer(configLayer)
      },
      test("cannot load successfully") {
        val invalidConfig: ConfigSource = TypesafeConfigSource
          .fromHoconString(
            s"""
               | server {
               |   port = "EightyEighty"
               | }
               |""".stripMargin
          )

        val invalidConfigLayer = ZLayer(read(configDescriptor from invalidConfig))

        val result: ReadError[String] = (for {
          env <- ZIO.environment[ApplicationConfig]
          appConfig = env.get[ApplicationConfig]
        } yield appConfig.server.port
          )
          .provideLayer(invalidConfigLayer)
          .flip
          .unsafeRun

        val resultingErrorMessage: String =
          """
            |FormatError(List(Key(server), Key(port)), Provided value is EightyEighty, expecting the type int, List(value of type int), Set())
            |"""
            .stripMargin
            .stripLineEnd
            .stripLeading()

        assertTrue(resultingErrorMessage == result.nonPrettyPrintedString)
      }
    )
}