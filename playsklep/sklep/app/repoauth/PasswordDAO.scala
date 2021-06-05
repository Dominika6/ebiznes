package repoauth
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import javax.inject.{Inject, Singleton}
import models.auth.{LoginInfoTable, Password, PasswordTable}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag

@Singleton
class PasswordDAO @Inject()(dbConfigProvider: DatabaseConfigProvider)
                           (implicit  val classTag: ClassTag[PasswordInfo], ec: ExecutionContext) extends DelegableAuthInfoDAO[PasswordInfo] {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  val _password = TableQuery[PasswordTable]
  val _loginInfo = TableQuery[LoginInfoTable]

  override def find(loginInfo: LoginInfo): Future[Option[PasswordInfo]] = db.run {
    findPasswordQuery(loginInfo).result.headOption.map(dbPwdOption => dbPwdOption.map(dbPwd =>
      PasswordInfo(dbPwd.hasher, dbPwd.hash, dbPwd.salt)))
  }

  override def add(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = {
    val action = _loginInfo.filter(dbLoginInfo => dbLoginInfo.providerId === loginInfo.providerID &&
      dbLoginInfo.providerKey === loginInfo.providerKey)
      .result
      .headOption
      .flatMap { dbLoginInfo =>
        _password += Password(dbLoginInfo.get.id, authInfo.hasher, authInfo.password, authInfo.salt)
      }.transactionally

    db.run(action).map(_ => authInfo)
  }

  override def update(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = db.run {
    updateAction(loginInfo, authInfo).map(_ => authInfo)
  }

  override def save(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = {
    val query = findLoginInfoQuery(loginInfo)
      .joinLeft(_password).on(_.id === _.loginInfoId)
    val action = query.result.head.flatMap {
      case (_, Some(_)) => updateAction(loginInfo, authInfo)
      case (_, None) => addAction(loginInfo, authInfo)
    }
    db.run(action).map(_ => authInfo)
  }

  override def remove(loginInfo: LoginInfo): Future[Unit] = db.run {
    findPasswordQuery(loginInfo).delete.map(_ => ())
  }

  def findLoginInfoQuery(loginInfo: LoginInfo) =
    _loginInfo.filter(dbLoginInfo => dbLoginInfo.providerId === loginInfo.providerID &&
      dbLoginInfo.providerKey === loginInfo.providerKey)

  def findPasswordQuery(loginInfo: LoginInfo) = {
    _password.filter(_.loginInfoId in findLoginInfoQuery(loginInfo).map(_.id))
  }

  def addAction(loginInfo: LoginInfo, authInfo: PasswordInfo) =
    findLoginInfoQuery(loginInfo).result.head.flatMap { dbLoginInfo =>
      _password += Password( dbLoginInfo.id, authInfo.hasher, authInfo.password, authInfo.salt)
    }.transactionally

  def updateAction(loginInfo: LoginInfo, passwordInfo: PasswordInfo) = {
    findPasswordQuery(loginInfo)
      .map(dbPasswordInfo => (dbPasswordInfo.hasher, dbPasswordInfo.hash, dbPasswordInfo.salt))
      .update((passwordInfo.hasher, passwordInfo.password, passwordInfo.salt))
  }
}
