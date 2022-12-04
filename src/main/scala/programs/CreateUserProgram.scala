package github.com.dpratt747
package programs

import hash.PasswordHashingAlg
import zio.*
import domain.*

import db.repositories.UserRepositoryAlg

import javax.sql.DataSource

trait CreateUserProgramAlg {
  def createUser(user: User): ZIO[Any, Throwable, Unit]
}

final case class CreateUserProgram(
    private val passwordHashingAlg: PasswordHashingAlg,
    private val userRepo: UserRepositoryAlg
) extends CreateUserProgramAlg {
  def createUser(user: User): ZIO[Any, Throwable, Unit] =
    for {
      hash <- passwordHashingAlg.hash(user.password.asString)
      newUser <- ZIO.attempt(user.copy(password = Password(hash.getResult)))
      _ <- userRepo.insertUser(newUser, Salt(hash.getSalt))
    } yield ()
}

object CreateUserProgram {
  val live: ZLayer[
    UserRepositoryAlg with PasswordHashingAlg,
    Nothing,
    CreateUserProgram
  ] =
    ZLayer.fromZIO {
      for {
        hashingAlg <- ZIO.service[PasswordHashingAlg]
        userRepo <- ZIO.service[UserRepositoryAlg]
      } yield CreateUserProgram(hashingAlg, userRepo)
    }
}
