package github.com.dpratt747
package endpoints

import config.*
import domain.*

import io.circe.generic.auto.*
import io.circe.jawn.*
import zhttp.http.*
import zhttp.test.*
import zio.*
import zio.config.typesafe.TypesafeConfigSource
import zio.config.{ConfigSource, ReadError, read}
import zio.test.*
import zio.test.Assertion.*

object EndpointsSpec extends ZIOSpecDefault {

  override def spec =
    suite("Get")(
      test("/example returns Ok") {
        val path: Path = Path.root ++ Path(Vector(
          Path.Segment("example"))
        )
        val request: Request = Request(url = URL(path))
        for {
          response <- Get.routes(request)
          body <- response.body.asArray
          decodedResponse <- ZIO.fromEither(decodeByteArray[ZioHttpExampleJsonResponse](body))
        } yield assertTrue(response.status == Status.Ok)
          && assertTrue(decodedResponse == ZioHttpExampleJsonResponse("Hello World!"))
      },
      test("/invalidRoute returns NotFound") {
        val path: Path = Path.root ++ Path(Vector(
          Path.Segment("invalidRoute"))
        )
        val request: Request = Request(url = URL(path))
        for {
          response <- Get.routes(request)
        } yield assertTrue(response.status == Status.NotFound)
      }
    )
}