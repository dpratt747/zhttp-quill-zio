package github.com.dpratt747
package helper

import zio.*
import zio.Runtime

extension [A](input: ZIO[Any, Any, A]) {
  def unsafeRun: A = Unsafe.unsafe { implicit unsafe =>
    Runtime.default.unsafe.run(input).getOrThrowFiberFailure()
  }
}

