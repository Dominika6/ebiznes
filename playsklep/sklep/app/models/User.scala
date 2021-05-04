package models

import play.api.libs.json.{Json, OFormat}

case class User(userId: String, firstName: String, surname: String, email: String, role: UserRoles.UserRole)

object UserRoles extends Enumeration {
  type UserRole = String
  val User = "user"
  val Admin = "admin"
}
object User {
  implicit val userFormat: OFormat[User] = Json.format[User]
}
