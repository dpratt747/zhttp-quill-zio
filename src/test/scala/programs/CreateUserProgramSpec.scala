package github.com.dpratt747
package programs

import config.*
import domain.*

import io.circe.generic.auto.*
import io.circe.jawn.*
import zhttp.http.*
import zhttp.test.*
import zio.{Task, *}
import zio.config.typesafe.TypesafeConfigSource
import zio.config.{ConfigSource, ReadError, read}
import zio.test.*
import zio.*
import zio.test.Assertion.*
import endpoints.*

import com.password4j.Hash
import github.com.dpratt747.hash.{PasswordHashing, PasswordHashingAlg}

object CreateUserProgramSpec extends ZIOSpecDefault {
  override def spec =
    suite("CreateUserProgram")(
      test("creates a user successfully") {

        val nonEmptyStringGen = Gen.stringBounded(0, 10)(Gen.printableChar)

        checkN(30)(Gen.string, Gen.string, Gen.string, nonEmptyStringGen) {
          case (
                userName: UserName @unchecked,
                firstName: FirstName @unchecked,
                lastName: LastName @unchecked,
                password: Password @unchecked
              ) =>
            (for {
              hashingAlg <- ZIO.service[PasswordHashingAlg]
              program = CreateUserProgram(hashingAlg)
              response <- program
                .createUser(User(userName, firstName, lastName, password))
                .isSuccess
            } yield assertTrue(response)).provide(PasswordHashing.live)
        }
      }
    )
}
