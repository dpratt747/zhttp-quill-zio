package github.com.dpratt747
package helper

import domain.*

import com.password4j.types.Bcrypt
import com.password4j.{AlgorithmFinder, BcryptFunction, Hash, HashingFunction}
import github.com.dpratt747.db.session.PostgresContextAlg
import github.com.dpratt747.hash.PasswordHashingAlg
import io.circe.*
import io.circe.jawn.decodeByteArray
import io.circe.syntax.*
import io.getquill.{PostgresZioJdbcContext, SnakeCase}
import zhttp.http.Body
import zio.*
import zio.Runtime

import java.sql.*
import javax.sql.DataSource
import java.io.PrintWriter
import java.sql.{Connection, ConnectionBuilder}
import java.util.concurrent.atomic.AtomicReference
import java.util.logging.Logger

extension[A] (input: ZIO[Any, Any, A]) {
  def unsafeRun: A = Unsafe.unsafe { implicit unsafe =>
    Runtime.default.unsafe.run(input).getOrThrowFiberFailure()
  }
}

val pgContextEmpty: ULayer[PostgresContextAlg] =
  ZLayer.succeed(new PostgresContextAlg {
    override def context: PostgresZioJdbcContext[SnakeCase.type] = ???
  })


val dsLayerEmpty: ULayer[DataSource] =
  ZLayer.succeed(new DataSource {

    override def getConnection(username: _root_.java.lang.String, password: _root_.java.lang.String): Connection = ???

    override def getConnection: Connection = ???

    override def getParentLogger: Logger = ???

    override def getLoginTimeout: RuntimeFlags = ???

    override def getLogWriter: PrintWriter = ???

    override def setLogWriter(out: PrintWriter): Unit = ???

    override def isWrapperFor(iface: Class[_]): Boolean = ???

    override def unwrap[T](iface: Class[T]): T = ???

    override def setLoginTimeout(seconds: RuntimeFlags): Unit = ???

    override def createConnectionBuilder(): ConnectionBuilder = super.createConnectionBuilder()

  })
