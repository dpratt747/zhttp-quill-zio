package github.com.dpratt747
package db.repositories

import github.com.dpratt747.db.session.*
import config.*
import github.com.dpratt747.db.migrations.*
import zio.test.Assertion.*
import zio.test.*
import domain.*
import config.*
import zio.*

object UserRepositorySpec extends ZIOSpecDefault {
  override def spec =
    suite("UserRepository")(
      test(
        "insert user persists the user and the salt used to create the password"
      ) {
        checkN(1)(
          Gen.stringBounded(5, 10)(Gen.char),
          Gen.stringBounded(5, 10)(Gen.char),
          Gen.stringBounded(5, 10)(Gen.char),
          Gen.alphaNumericString,
          Gen.alphaNumericString
        ) {
          case (
                userName: UserName @unchecked,
                firstName: FirstName @unchecked,
                lastName: LastName @unchecked,
                password: Password @unchecked,
                salt: Salt @unchecked
              ) =>
            (for {
              service: UserRepositoryAlg <- ZIO.service[UserRepositoryAlg]
              _ <- service
                .insertUser(User(userName, firstName, lastName, password), salt)
            } yield assertTrue(true))
              .provide(UserRepository.live, PostgresContext.live, configLayer)

        }

      }
    ) @@ TestAspect.beforeAll(
      ZIO
        .serviceWithZIO[FlywayMigrationsAlg](_.migrate)
        .provide(FlywayMigrations.live, configLayer)
    ) @@ TestAspect.afterAll(
      ZIO
        .serviceWithZIO[FlywayMigrationsAlg](_.clean)
        .provide(FlywayMigrations.live, configLayer)
        .ignore
    )
}
