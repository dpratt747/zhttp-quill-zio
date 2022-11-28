package github.com.dpratt747
package helper

import domain.*
import io.circe.*
import io.circe.jawn.decodeByteArray
import io.circe.syntax.*
import zhttp.http.Body
import zio.*
import zio.Runtime

extension[A] (input: ZIO[Any, Any, A]) {
  def unsafeRun: A = Unsafe.unsafe { implicit unsafe =>
    Runtime.default.unsafe.run(input).getOrThrowFiberFailure()
  }
}