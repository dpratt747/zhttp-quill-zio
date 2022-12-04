package github.com.dpratt747
package endpoints

import config.*
import db.session.{PostgresContext, PostgresContextAlg}
import domain.*
import endpoints.decodeJsonBody
import helper.*
import programs.CreateUserProgramAlg

import com.zaxxer.hikari.util.DriverDataSource
import io.circe.*
import io.circe.generic.auto.*
import io.circe.jawn.*
import io.circe.syntax.*
import io.getquill.{PostgresZioJdbcContext, SnakeCase}
import zhttp.http.*
import zhttp.test.*
import zio.*
import zio.config.typesafe.TypesafeConfigSource
import zio.config.{ConfigSource, ReadError, read}
import zio.test.*
import zio.test.Assertion.*

import java.util.concurrent.atomic.AtomicReference

object PostEndpointsSpec extends ZIOSpecDefault {

  override def spec =
    suite("Post")(
      test("/user returns Ok") {
        check(Gen.string, Gen.string, Gen.string, Gen.alphaNumericString) {
          case (
                userName: UserName @unchecked,
                firstName: FirstName @unchecked,
                lastName: LastName @unchecked,
                password: Password @unchecked
              ) =>
            val path: Path = Path.root ++ Path(Vector(Path.Segment("user")))
            val request: Request = Request(
              method = Method.POST,
              url = URL(path),
              body = Body.fromString(
                User(userName, firstName, lastName, password).toJsonString
              )
            )
            val count = AtomicReference(0)

            val createUserProgramAlgLayer: ULayer[CreateUserProgramAlg] =
              ZLayer.succeed(new CreateUserProgramAlg {
                override def createUser(user: User): ZIO[Any, Throwable, Unit] =
                  for {
                    _ <- ZIO.attempt(count.getAndUpdate(_ + 1))
                  } yield ()
              })

            (for {
              post <- ZIO.service[PostAlg]
              response <- post.routes(request)
              counter <- ZIO.attempt(count.get())
            } yield assertTrue(response.status == Status.Ok && counter == 1))
              .provide(
                Post.live,
                dsLayerEmpty,
                createUserProgramAlgLayer
              )
        }
      },
      test("/invalidRoute returns NotFound") {

        val createUserProgramAlgLayer: ULayer[CreateUserProgramAlg] =
          ZLayer.succeed(new CreateUserProgramAlg {
            override def createUser(user: User): ZIO[Any, Throwable, Unit] = ???
          })

        val path: Path = Path.root ++ Path(Vector(Path.Segment("invalidRoute")))
        val request: Request = Request(url = URL(path))

        (for {
          post <- ZIO.service[PostAlg]
          response <- post.routes(request)
        } yield assertTrue(response.status == Status.NotFound))
          .provide(Post.live, dsLayerEmpty, createUserProgramAlgLayer)
      },
      test("/user returns 500 when the creation program returns an error") {
        check(Gen.string, Gen.string, Gen.string, Gen.alphaNumericString) {
          case (
                userName: UserName @unchecked,
                firstName: FirstName @unchecked,
                lastName: LastName @unchecked,
                password: Password @unchecked
              ) =>
            val path: Path = Path.root ++ Path(Vector(Path.Segment("user")))

            val request: Request = Request(
              method = Method.POST,
              url = URL(path),
              body = Body.fromString(
                User(userName, firstName, lastName, password).toJsonString
              )
            )

            val count = AtomicReference(0)

            val createUserProgramAlgLayer: ULayer[CreateUserProgramAlg] =
              ZLayer.succeed(new CreateUserProgramAlg {
                override def createUser(user: User): ZIO[Any, Throwable, Unit] =
                  for {
                    _ <- ZIO.attempt(count.getAndUpdate(_ + 1))
                    _ <- ZIO.fail(new RuntimeException("Boom"))
                  } yield ()
              })

            (for {
              post <- ZIO.service[PostAlg]
              response <- post.routes(request)
              counter <- ZIO.attempt(count.get())
            } yield assertTrue(
              response.status == Status.BadGateway && counter == 1
            ))
              .provide(
                Post.live,
                dsLayerEmpty,
                createUserProgramAlgLayer
              )
        }
      },
      test(
        "/user returns invalid request when an incorrect Json body is provided"
      ) {

        val path: Path = Path.root ++ Path(Vector(Path.Segment("user")))

        val request: Request = Request(
          method = Method.POST,
          url = URL(path),
          body = Body.fromString(
            """{"userName":"","firstName":"譨뒖捚薣᭮쾧","lastName":"謁겭"}"""
          )
        )

        val count = AtomicReference(0)

        val createUserProgramAlgLayer: ULayer[CreateUserProgramAlg] =
          ZLayer.succeed(new CreateUserProgramAlg {
            override def createUser(user: User): ZIO[Any, Throwable, Unit] =
              for {
                _ <- ZIO.attempt(count.getAndUpdate(_ + 1))
                _ <- ZIO.fail(new RuntimeException("Boom"))
              } yield ()
          })

        (for {
          post <- ZIO.service[PostAlg]
          response <- post.routes(request)
          counter <- ZIO.attempt(count.get())
        } yield assertTrue(
          response.status == Status.BadRequest && counter == 0
        ))
          .provide(
            Post.live,
            dsLayerEmpty,
            createUserProgramAlgLayer
          )
      }
    )

}
