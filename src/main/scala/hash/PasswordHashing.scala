package github.com.dpratt747
package hash

import com.password4j.*
import zio.*

trait PasswordHashingAlg {
  def hash(password: String): Task[Hash]
}

final case class PasswordHashing() extends PasswordHashingAlg {
  def hash(password: String): ZIO[Any, Throwable, Hash] =
    for {
      hashed <- ZIO.attempt(
        Password
          .hash(password)
          .addRandomSalt(32)
          .withArgon2()
      )
    } yield hashed
}

object PasswordHashing {
  val live: ULayer[PasswordHashingAlg] = ZLayer.succeed(
    PasswordHashing()
  )
}
