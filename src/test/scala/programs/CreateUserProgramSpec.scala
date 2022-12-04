package github.com.dpratt747
package programs

import config.*
import db.repositories.UserRepositoryAlg
import db.session.PostgresContext
import domain.*
import endpoints.*
import hash.{PasswordHashing, PasswordHashingAlg}
import helper.*

import com.password4j.{AlgorithmFinder, Hash, HashingFunction}
import com.zaxxer.hikari.util.DriverDataSource
import io.circe.generic.auto.*
import io.circe.jawn.*
import zhttp.http.*
import zhttp.test.*
import zio.config.typesafe.TypesafeConfigSource
import zio.config.{ConfigSource, ReadError, read}
import zio.test.*
import zio.test.Assertion.*
import zio.*

import java.util.concurrent.atomic.AtomicReference
import javax.sql.DataSource

object CreateUserProgramSpec extends ZIOSpecDefault {
  override def spec =
    suite("CreateUserProgram")(
      test("creates a user successfully") {

        val nonEmptyStringGen = Gen.stringBounded(1, 10)(Gen.printableChar)

        check(
          nonEmptyStringGen,
          nonEmptyStringGen,
          nonEmptyStringGen,
          nonEmptyStringGen
        ) {
          case (
                userName: UserName @unchecked,
                firstName: FirstName @unchecked,
                lastName: LastName @unchecked,
                password: Password @unchecked
              ) =>
            val count: AtomicReference[RuntimeFlags] = AtomicReference(0)
            val hashingCount: AtomicReference[RuntimeFlags] = AtomicReference(0)

            val userRepo: UserRepositoryAlg = (user: User, salt: Salt) =>
              for {
                _ <- ZIO.attempt(count.getAndUpdate(_ + 1))
              } yield ()

            val passwordHashingEmpty = ZLayer.succeed(new PasswordHashingAlg {
              override def hash(password: _root_.java.lang.String): Task[Hash] = {
                val pass = "password"
                val hashingFunction: HashingFunction = AlgorithmFinder.getBcryptInstance
                for {
                  _ <- ZIO.attempt(hashingCount.getAndUpdate(_ + 1))
                  hash <- ZIO.attempt(new Hash(hashingFunction, pass, pass.getBytes, pass))
                } yield hash
              }
            })

            (for {
              hashingAlg <- ZIO.service[PasswordHashingAlg]
              program = CreateUserProgram(hashingAlg, userRepo)
              result <- program
                .createUser(User(userName, firstName, lastName, password))
                .isSuccess
              counter = count.get()
              hashingCounter = hashingCount.get()
            } yield assertTrue(result && counter == 1 && hashingCounter == 1))
              .provide(passwordHashingEmpty)
        }
      }
    )
}
