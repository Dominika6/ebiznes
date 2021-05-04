package models

import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

  val user = TableQuery[UserTable]

  class UserTable(tag: Tag) extends Table[User](tag, "user") {
    def userId = column[String]("userId", O.PrimaryKey)
    def firstName = column[String]("firstName")
    def surname = column[String]("surname")
    def email = column[String]("email")
    def role = column[String]("role")
    def * = (userId, firstName, surname, email, role) <> ((User.apply _).tupled, User.unapply)
  }

  def create(firstName: String, surname: String, email:String, role: UserRoles.UserRole): Future[Int] = db.run {
    val userId: String = UUID.randomUUID().toString
    user.insertOrUpdate(User(userId, firstName, surname, email, role))
  }

  def getAll: Future[Seq[User]] = db.run {
    user.result
  }

  def delete(userId: String): Future[Int] = db.run {
    user.filter(_.userId === userId).delete
  }

  def update(userId: String, firstName: String, surname: String, email: String, role: UserRoles.UserRole): Future[Int] = db.run {
    user.filter(_.userId === userId).update(User(userId, firstName, surname, email, role))
  }
}
