package github.com.dpratt747
package config

import config.*
import domain.*
import helper.*

import zio.*
import zio.config.typesafe.TypesafeConfigSource
import zio.config.{ConfigSource, ReadError, read}
import zio.test.*
import zio.test.Assertion.*

object ConfigSpec extends ZIOSpecDefault {
  override def spec: Spec[Any, ReadError[String]] =
    suite("ApplicationConfig")(
      test("can load successfully") {
        (for {
          appConfig <- ZIO.service[ApplicationConfig]
        } yield assertTrue(appConfig.server.port.asInt == 8080))
          .provide(configLayer)
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

        val invalidConfigLayer = ZLayer(read(applicationConfigDescriptor from invalidConfig))

        val result: ReadError[String] = (for {
          appConfig <- ZIO.service[ApplicationConfig]
        } yield appConfig.server.port
          )
          .provide(invalidConfigLayer)
          .flip
          .unsafeRun

        val resultingErrorMessage: String =
          """
            |FormatError(List(Key(server), Key(port)), Provided value is EightyEighty, expecting the type int, List(value of type int), Set())
            |"""
            .stripMargin
            .stripLineEnd
            .stripLeading()

        assertTrue(result.nonPrettyPrintedString.contains(resultingErrorMessage))
      }
    )
}