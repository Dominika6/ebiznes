package repoauth
import java.util.UUID
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.providers.OAuth2Info
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import javax.inject.{Inject, Singleton}
import models.auth._
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag

class OAuth2InfoDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
                             (implicit ec: ExecutionContext, val classTag: ClassTag[OAuth2Info]) extends DelegableAuthInfoDAO[OAuth2Info] {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  val _user = TableQuery[UserTable]
  val _loginInfo = TableQuery[LoginInfoTable]
  val _oauth2InfoTable = TableQuery[OAuth2InfoTable]

  override def find(loginInfo: LoginInfo): Future[Option[OAuth2Info]] = ???

  override def add(loginInfo: LoginInfo, authInfo: OAuth2Info): Future[OAuth2Info] = {
    val foundLoginInfo = _loginInfo.filter(dbLoginInfo => dbLoginInfo.providerId === loginInfo.providerID &&
      dbLoginInfo.providerKey === loginInfo.providerKey).result.headOption;

    val action = foundLoginInfo.flatMap { dbLoginInfo =>
      _oauth2InfoTable.filter(_.loginInfoId === dbLoginInfo.get.id).result.headOption.flatMap {
        case Some(o) => {
          _oauth2InfoTable.filter(_.id === o.id).update(OAuth2InfoDb(
            o.id, authInfo.accessToken, authInfo.tokenType, authInfo.expiresIn,
            authInfo.refreshToken, dbLoginInfo.get.id
          ))
        }
        case None => {
          val id = UUID.randomUUID().toString
          _oauth2InfoTable += OAuth2InfoDb(id, authInfo.accessToken, authInfo.tokenType, authInfo.expiresIn,
            authInfo.refreshToken, dbLoginInfo.get.id)
        }
      }
    }.transactionally

    db.run(action).map(_ => authInfo)
  }

  override def update(loginInfo: LoginInfo, authInfo: OAuth2Info): Future[OAuth2Info] = ???

  override def save(loginInfo: LoginInfo, authInfo: OAuth2Info): Future[OAuth2Info] = ???

  override def remove(loginInfo: LoginInfo): Future[Unit] = ???

}