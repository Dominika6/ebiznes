package controllers.api
import com.mohiva.play.silhouette.api.Silhouette
import javax.inject.{Inject, Singleton}
import play.api.libs.json.{JsError, JsValue, Json, OFormat}
import play.api.mvc._
import models.PayRepository
import models.auth.UserRoles
import utils.auth.{JsonErrorHandler, JwtEnv, RoleJWTAuthorization}
import scala.concurrent.ExecutionContext

case class CreatePay(name: String)

object CreatePay {
  implicit val payFormat: OFormat[CreatePay] = Json.format[CreatePay]
}

case class UpdatePay(name: Option[String])

object UpdatePay {
  implicit val payFormat: OFormat[UpdatePay] = Json.format[UpdatePay]
}


@Singleton
class PayApiController @Inject()(
                                      payRepository: PayRepository,
                                      errorHandler: JsonErrorHandler,
                                      silhouette: Silhouette[JwtEnv],
                                      cc: MessagesControllerComponents
                                    )(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val payNotFound: Result = NotFound(Json.obj("message" -> "Pay does not exist"))

  def getAll: Action[AnyContent] = Action.async { implicit request =>
    val pays = payRepository.getAll
    pays.map(pay => Ok(Json.toJson(pay)))
  }

  def get(id: String): Action[AnyContent] = Action.async { implicit request =>
    payRepository.getById(id) map {
      case Some(p) => Ok(Json.toJson(p))
      case None => payNotFound
    }
  }

  def create(): Action[AnyContent] = silhouette.SecuredAction(RoleJWTAuthorization(UserRoles.Admin)) { implicit request =>
    val body = request.body.asJson.get
    body.validate[CreatePay].fold(
      errors => {
        BadRequest(Json.obj("message" -> JsError.toJson(errors)))
      },
      pay => {
        payRepository.create(pay.name)
        Ok(Json.obj("message" -> "Pay created"))
      }
    )
  }

  def update(id: String): Action[JsValue] = silhouette.SecuredAction(RoleJWTAuthorization(UserRoles.Admin)).async(parse.json) { implicit request =>
    payRepository.getById(id) map {
      case Some(p) =>
        val body = request.body
        body.validate[UpdatePay].fold(
          errors => {
            BadRequest(Json.obj("message" -> JsError.toJson(errors)))
          },
          pay => {
            payRepository.update(id, pay.name.getOrElse(p.method))
            Ok(Json.obj("message" -> "Pay updated"))
          }
        )
      case None => payNotFound
    }
  }

  def delete(id: String): Action[AnyContent] = silhouette.SecuredAction(RoleJWTAuthorization(UserRoles.Admin)).async { implicit request =>
    payRepository.getById(id) map {
      case Some(g) =>
        payRepository.delete(id)
        Ok(Json.obj("message" -> "Pay deleted"))
      case None => payNotFound
    }
  }

}