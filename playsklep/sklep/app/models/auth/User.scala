package models.auth
import com.mohiva.play.silhouette.api.Identity
import play.api.libs.json.{Json, OFormat}
import slick.jdbc.SQLiteProfile.api._

object UserRoles extends Enumeration {
  type UserRole = String
  val User = "user"
  val Admin = "admin"
}

case class User(userId: String,
                firstName: String,
                surname: String,
                email: String,
                role: UserRoles.UserRole
               ) extends Identity

class UserTable(tag: Tag) extends Table[User](tag, "user") {
  def userId = column[String]("userId", O.PrimaryKey)
  def firstName = column[String]("firstName")
  def surname = column[String]("surname")
  def email = column[String]("email")
  def role = column[String]("role")
  def * = (userId, firstName, surname, email, role) <> ((User.apply _).tupled, User.unapply)
}

object User {
  implicit val userFormat = Json.format[User]
//  implicit val userFormat: OFormat[User] = Json.format[User]
}

case class Password(loginInfoId: String,
                    hasher: String,
                    hash: String,
                    salt: Option[String])


class PasswordTable(tag: Tag) extends Table[Password](tag, "password") {
  def loginInfoId = column[String]("loginInfoId", O.PrimaryKey)
  def hasher = column[String]("hasher")
  def hash = column[String]("hash")
  def salt = column[Option[String]]("salt")
  def * = (loginInfoId, hasher, hash, salt) <> ((Password.apply _).tupled, Password.unapply)
}

object Password {
  implicit val passwordFormat: OFormat[Password] = Json.format[Password]
}