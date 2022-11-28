package github.com.dpratt747
package db.domain

final case class HashSaltTable(id: Option[Int], userId: Int, salt: String)
final case class UserTable(id: Option[Int], userName: String, firstName: String, lastName: String, passwordHash: String)