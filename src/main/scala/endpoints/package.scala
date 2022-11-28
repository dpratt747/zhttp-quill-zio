package github.com.dpratt747
package endpoints

import domain.{DomainJson, JsonResponse}

import io.circe.*
import io.circe.jawn.decodeByteArray
import io.circe.syntax.*
import zhttp.http.Body
import zio.ZIO

extension[A <: DomainJson] (input: A)(using a: Encoder[A]) {
  def toJsonString: String = input.asJson.noSpaces
}

extension (input: Body) {
  def decodeJsonBody[A <: DomainJson](using a: Decoder[A]): ZIO[Any, Throwable, A] =
    for {
      byteArray <- input.asArray
      decodedResponse <- ZIO.fromEither(decodeByteArray[A](byteArray))
    } yield decodedResponse
}