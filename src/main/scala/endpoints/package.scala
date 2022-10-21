package github.com.dpratt747
package endpoints

import domain.JsonResponseType

import io.circe.*
import io.circe.syntax.*

extension [A <: JsonResponseType](input: A) {
  def toJsonString(implicit a : Encoder[A]) = input.asJson.noSpaces
}