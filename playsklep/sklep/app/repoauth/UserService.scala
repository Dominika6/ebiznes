package repoauth

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.IdentityService
import com.mohiva.play.silhouette.api.util.PasswordHasher
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import javax.inject.{Inject, Singleton}
import models.auth.{LoginInfoDb, LoginInfoTable, PasswordTable, User, UserRoles, UserTable}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class UserService @Inject()(dbConfigProvider: DatabaseConfigProvider,
                            passwordHasher: PasswordHasher,
                            userDao: UserDAO,
                            loginInfoDao: LoginInfoDAO,
                            authInfoRepository: AuthInfoRepository)
                           (implicit ec: ExecutionContext) extends IdentityService[User] {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  val _user = TableQuery[UserTable]
  val _loginInfo = TableQuery[LoginInfoTable]
  val _password = TableQuery[PasswordTable]

  override def retrieve(loginInfo: LoginInfo) = {
    userDao.find(loginInfo)
  }

  def saveOrUpdate(firstName: String, surname: String, email: String, loginInfo: LoginInfo) = {
    userDao.find(loginInfo).flatMap {
      case Some(foundUser) => userDao.update(foundUser.userId, firstName, surname, email, foundUser.role)
      case None => save(firstName, surname, email, loginInfo)
    }
  }

  def save(firstName: String, surname: String, email: String, loginInfo: LoginInfo) = {
    for {
      savedUserId <- userDao.save(firstName, surname, email)
      _ <- loginInfoDao.saveUserLoginInfo(savedUserId, loginInfo)
    } yield savedUserId
  }

  def getAll(): Future[Seq[User]] = db.run {
    _user.result
  }

  def getByEmail(email: String): Future[Option[User]] = db.run {
    _user.filter(_.email === email).result.headOption
  }

  def isExist(userId: String): Future[Boolean] = db.run {
    _user.filter(_.userId === userId).exists.result
  }

  def getById(userId: String): Future[Option[User]] = db.run {
    _user.filter(_.userId === userId).result.headOption
  }

  def delete(userId: String): Future[Int] = db.run {
    val user: User = Await.result(getById(userId), Duration.Inf).get
    _user.filter(_.userId === userId).delete
  }

  def update(id: String, firstName: String, surname: String, email: String, role: UserRoles.UserRole): Future[Int] = db.run {
    _user.filter(_.userId === id).update(User(id, firstName, surname, email, role))
  }

}

