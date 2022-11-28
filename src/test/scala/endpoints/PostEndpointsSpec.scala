package github.com.dpratt747
package endpoints

import config.*
import domain.*
import endpoints.decodeJsonBody
import io.circe.*
import io.circe.generic.auto.*
import io.circe.syntax.*
import io.circe.jawn.*
import zhttp.http.*
import zhttp.test.*
import zio.*
import zio.config.typesafe.TypesafeConfigSource
import zio.config.{ConfigSource, ReadError, read}
import zio.test.*
import zio.test.Assertion.*
import io.circe.generic.auto.*


object PostEndpointsSpec extends ZIOSpecDefault {

  override def spec =
    suite("Post")(
      test("/user returns Ok") {
        check(Gen.string, Gen.string, Gen.string, Gen.alphaNumericString) {
          case (
            userName: UserName@unchecked,
            firstName: FirstName@unchecked,
            lastName: LastName@unchecked,
            password: Password@unchecked
            ) =>
            val path: Path = Path.root ++ Path(Vector(
              Path.Segment("user"))
            )
            val request: Request = Request(
              method = Method.POST,
              url = URL(path),
              body = Body.fromString(
                User(userName, firstName, lastName, password).toJsonString
              )
            )
            for {
              response <- Post.routes(request)
//              body <- response.body.decodeJsonBody[ZioHttpExampleJsonResponse]
//              decodedResponse <- ZIO.fromEither(decodeByteArray[ZioHttpExampleJsonResponse](body))
            } yield assertTrue(response.status == Status.Ok)
          //          && assertTrue(decodedResponse == ZioHttpExampleJsonResponse("Hello World!"))
        }
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