package github.com.dpratt747
package domain

import io.circe.{ Decoder, Encoder }, io.circe.generic.auto._

sealed trait JsonResponseType extends Product with Serializable
final case class ZioHttpExampleJsonResponse(response: String) extends JsonResponseType
