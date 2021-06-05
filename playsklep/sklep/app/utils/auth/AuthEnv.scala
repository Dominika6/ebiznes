package utils.auth
import com.mohiva.play.silhouette.api.Env
import com.mohiva.play.silhouette.impl.authenticators.{CookieAuthenticator, JWTAuthenticator}
import models.auth.User

trait CookieEnv extends Env {
  type I = User
  type A = CookieAuthenticator
}

trait JwtEnv extends Env {
  type I = User
  type A = JWTAuthenticator
}