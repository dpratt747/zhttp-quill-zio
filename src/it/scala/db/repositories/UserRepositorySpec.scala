package github.com.dpratt747
package db.repositories

import config.{ApplicationConfig, configLayer}
import db.client.MongoDBX
import domain.*

import com.mongodb.client.model.*
import com.mongodb.client.*
import org.bson.Document
import zio.config.typesafe.TypesafeConfigSource
import zio.config.{ConfigSource, ReadError, read}
import zio.test.*
import zio.test.Assertion.*
import zio.{Scope, ZIO, ZLayer}

import scala.jdk.CollectionConverters.*

object UserRepositorySpec extends ZIOSpecDefault {
  override def spec =
    suite("UserRepository")(
      test("insert user persists the user and the salt used to create the password") {
        checkN(1)(
          Gen.stringBounded(5, 10)(Gen.char),
          Gen.stringBounded(5, 10)(Gen.char),
          Gen.stringBounded(5, 10)(Gen.char),
          Gen.alphaNumericString
        ) {
          case (
            userName: UserName@unchecked,
            firstName: FirstName@unchecked,
            lastName: LastName@unchecked,
            password: Password@unchecked
            ) =>

            val action: TransactionBody[ZIO[ApplicationConfig with MongoClient & ClientSession, Throwable, FindIterable[Document]]] =
              () => for {
                client <- ZIO.service[MongoClient]
                session <- ZIO.service[ClientSession]
                config <- ZIO.serviceWith[ApplicationConfig](_.mongo)
                db <- ZIO.attempt(client.getDatabase(config.database.asString))
                userCollection <- ZIO.attempt(db.getCollection("user"))
                _ <- UserRepository.createUser(CreateUser(userName, firstName, lastName, password), session)
                resDoc <- ZIO.attempt(userCollection.find(Filters.eq("userName", userName)))
              } yield resDoc

            val clientSession: ZLayer[MongoClient & Scope, Throwable, ClientSession] =
              ZLayer.fromZIO(for {
                client <- ZIO.service[MongoClient]
                session <- ZIO.acquireRelease(ZIO.attempt(client.startSession()))(session => ZIO.attempt(session.abortTransaction()).ignore.tap(_ => ZIO.succeed(println("aborting"))) *> ZIO.attempt(session.close()).ignore)
              } yield session)

            (for {
//              client <- ZIO.service[MongoClient]
              session <- ZIO.service[ClientSession]
              resultDoc <- session.withTransaction(action)
              _ = println(resultDoc.asScala.toVector.head) // get user by username
            } yield assertTrue(true))
              .provide(MongoDB.clientLayer, configLayer, Scope.default, clientSession)
        }
      }
    )
}