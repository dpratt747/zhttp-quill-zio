package github.com.dpratt747
package programs

import github.com.dpratt747.hash.PasswordHashingAlg
import zio.*
import domain.*

trait CreateUserProgramAlg {
  def createUser(user: User): Task[Unit]
}

final case class CreateUserProgram(
    private val passwordHashingAlg: PasswordHashingAlg
) extends CreateUserProgramAlg {
  override def createUser(user: User): Task[Unit] =
    for {
      hash <- passwordHashingAlg.hash(user.password.asString)
      newUser <- ZIO.attempt(user.copy(password = Password(hash.getResult)))
      _ = println(newUser)
      _ = println(hash.getSalt)
    } yield ()
}

object CreateUserProgram {
  val live: ZLayer[PasswordHashingAlg, Nothing, CreateUserProgram] = ZLayer.fromZIO{
    for {
      hashingAlg <- ZIO.service[PasswordHashingAlg]
    } yield CreateUserProgram(hashingAlg)
  }
}