package utils.auth
import com.mohiva.play.silhouette.api.{Authorization}
import com.mohiva.play.silhouette.impl.authenticators.{CookieAuthenticator, JWTAuthenticator}
import models.auth.{User, UserRoles}
import models.auth.UserRoles.UserRole
import play.api.mvc.Request
import scala.concurrent.Future

case class RoleJWTAuthorization(role: UserRole) extends Authorization[User, JWTAuthenticator] {
  def isAuthorized[B](user: User, authenticator: JWTAuthenticator)(implicit request: Request[B]) = {
    if (user.role == UserRoles.Admin) {
      Future.successful(true)
    } else {
      Future.successful(user.role == role)
    }
  }
}

case class RoleCookieAuthorization(role: UserRole) extends Authorization[User, CookieAuthenticator] {
  def isAuthorized[B](user: User, authenticator: CookieAuthenticator)(implicit request: Request[B]) = {
    if (user.role == UserRoles.Admin) {
      Future.successful(true)
    } else {
      Future.successful(user.role == role)
    }
  }
}