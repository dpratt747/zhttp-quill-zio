// package github.com.dpratt747
// package db.repositories
//
// import config.ApplicationConfig
// import domain.*
// import endpoints.*
//
// import io.circe.generic.auto.*
// import zio.*
//
// trait UserRepositoryAlg {
//  def insertUser(createUser: User, salt: String): ZIO[ApplicationConfig & PostgresSe & ClientSession, Throwable, Unit]
// }
//
