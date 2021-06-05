package repoauth
import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import javax.inject.{Inject, Singleton}
import models.auth.{LoginInfoTable, User, UserLoginInfoTable, UserRoles, UserTable}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

@Singleton
class UserDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
                       (implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  val _user = TableQuery[UserTable]
  val _loginInfo = TableQuery[LoginInfoTable]
  val _userLoginInfo = TableQuery[UserLoginInfoTable]

  def save(firstName: String, surname: String, email: String) = db.run {
    val userId: String = UUID.randomUUID().toString
    _user.insertOrUpdate(User(userId, firstName, surname, email, UserRoles.User)).map(_ => userId)
  }

  def update(userId: String, firstName: String, surname: String, email: String, role: UserRoles.UserRole) = db.run {
    _user.filter(_.userId === userId).update(User(userId, firstName, surname, email, role))
  }

  def find(loginInfo: LoginInfo) = {
    val findLoginInfoQuery = _loginInfo.filter(dbLoginInfo =>
      dbLoginInfo.providerId === loginInfo.providerID &&  dbLoginInfo.providerKey === loginInfo.providerKey)
    val query = for {
      dbLoginInfo <- findLoginInfoQuery
      dbUserLoginInfo <- _userLoginInfo.filter(_.loginInfoId === dbLoginInfo.id)
      dbUser <- _user.filter(_.userId === dbUserLoginInfo.userId)
    } yield dbUser
    db.run(query.result.headOption).map { dbUserOption =>
      dbUserOption.map { user =>
        User(user.userId, user.firstName, user.surname, user.email, user.role)
      }
    }
  }
}
