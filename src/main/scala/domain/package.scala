package github.com.dpratt747
package domain

import io.circe.generic.auto.*
import io.circe.{Decoder, Encoder}

opaque type UserName = String
opaque type FirstName = String
opaque type LastName = String
opaque type Password = String
opaque type ServerName = String
opaque type Host = String
opaque type ResponseString = String
opaque type Port = Int
opaque type ConnectionTimeout = Int
opaque type Database = String
opaque type Salt = String

implicit val responseStringEncoder: Encoder[ResponseString] =
  Encoder.encodeString.contramap[ResponseString](identity)
implicit val responseStringDecoder: Decoder[ResponseString] =
  Decoder.decodeString.map[ResponseString](identity)

implicit val userNameEncoder: Encoder[UserName] =
  Encoder.encodeString.contramap[UserName](identity)
implicit val userNameDecoder: Decoder[UserName] =
  Decoder.decodeString.map[UserName](identity)

implicit val saltEncoder: Encoder[Salt] =
  Encoder.encodeString.contramap[Salt](identity)
implicit val saltDecoder: Decoder[Salt] =
  Decoder.decodeString.map[Salt](identity)

implicit val serverNameEncoder: Encoder[ServerName] =
  Encoder.encodeString.contramap[ServerName](identity)
implicit val serverNameDecoder: Decoder[ServerName] =
  Decoder.decodeString.map[ServerName](identity)

implicit val firstNameEncoder: Encoder[FirstName] =
  Encoder.encodeString.contramap[FirstName](identity)
implicit val firstNameDecoder: Decoder[FirstName] =
  Decoder.decodeString.map[FirstName](identity)

implicit val lastNameEncoder: Encoder[LastName] =
  Encoder.encodeString.contramap[LastName](identity)
implicit val lastNameDecoder: Decoder[LastName] =
  Decoder.decodeString.map[LastName](identity)

implicit val passwordEncoder: Encoder[Password] =
  Encoder.encodeString.contramap[Password](identity)
implicit val passwordDecoder: Decoder[Password] =
  Decoder.decodeString.map[Password](identity)

object UserName {
  def apply(string: String): UserName = string
}

object FirstName {
  def apply(string: String): FirstName = string
}

object LastName {
  def apply(string: String): LastName = string
}

object Password {
  def apply(string: String): Password = string
}

object Salt {
  def apply(string: String): Salt = string
}

object ResponseString {
  def apply(string: String): ResponseString = string
}

object Port {
  def apply(int: Int): Port = int
}

object ConnectionTimeout {
  def apply(int: Int): ConnectionTimeout = int
}

object Host {
  def apply(string: String): Host = string
}

object Database {
  def apply(string: String): Database = string
}

object ServerName {
  def apply(string: String): ServerName = string
}

extension (input: UserName | FirstName | LastName | Password | Database | Host | ServerName | Salt) {
  def asString: String = input
}

extension (input: Port | ConnectionTimeout) {
  def asInt: Int = input
}

sealed trait DomainJson extends Product with Serializable

sealed trait JsonResponse extends DomainJson

sealed trait Payload extends DomainJson

final case class ZioHttpExampleJsonResponse(response: ResponseString)
    extends JsonResponse

final case class User(
    userName: UserName,
    firstName: FirstName,
    lastName: LastName,
    password: Password
) extends Payload
