package utils.auth
import com.mohiva.play.silhouette.api.actions.SecuredErrorHandler
import javax.inject.Singleton
import play.api.libs.json.Json
import play.api.mvc.Results._
import play.api.mvc._
import scala.concurrent.Future

@Singleton
class JsonErrorHandler extends SecuredErrorHandler {
  override def onNotAuthenticated(implicit request: RequestHeader) = {
    Future.successful(Unauthorized(Json.obj("message" -> "Authentication failed")))
  }

  override def onNotAuthorized(implicit request: RequestHeader) = {
    Future.successful(Forbidden(Json.obj("message" -> "Unauthorized")))
  }

}
