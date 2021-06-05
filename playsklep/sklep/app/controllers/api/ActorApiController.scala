package controllers.api
import com.mohiva.play.silhouette.api.Silhouette
import models.auth.UserRoles
import javax.inject.{Inject, Singleton}
import play.api.libs.json.{JsError, Json, OFormat}
import play.api.mvc._
import models.{ActorRepository, MovieRepository}
import utils.auth.{JsonErrorHandler, JwtEnv, RoleJWTAuthorization}
import scala.concurrent.ExecutionContext

case class CreateActor(firstName: String,
                       surname: String)

object CreateActor {
  implicit val actorFormat: OFormat[CreateActor] = Json.format[CreateActor]
}


case class UpdateActor(firstName: Option[String],
                       surname: Option[String])

object UpdateActor {
  implicit val actorFormat: OFormat[UpdateActor] = Json.format[UpdateActor]
}


@Singleton
class ActorApiController @Inject()(
                                    actorRepository: ActorRepository,
                                    movieRepository: MovieRepository,
                                    errorHandler: JsonErrorHandler,
                                    silhouette: Silhouette[JwtEnv],
                                    cc: MessagesControllerComponents
                                  )(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val actorNotFound: Result = NotFound(Json.obj("message" -> "Actor does not exist"));

  def getAll: Action[AnyContent] = Action.async { implicit request =>
    val actors = actorRepository.getAll;
    actors.map(actor => Ok(Json.toJson(actor)))
  }

  def get(id: String): Action[AnyContent] = Action.async { implicit request =>
    actorRepository.getById(id) map {
      case Some(a) => Ok(Json.toJson(a))
      case None => actorNotFound
    }
  }

  def getMovies(id: String): Action[AnyContent] = Action.async { implicit request =>
    val movies = movieRepository.getForActor(id)
    movies.map(movie => Ok(Json.toJson(movie)))
  }

  def create(): Action[AnyContent] = silhouette.SecuredAction(RoleJWTAuthorization(UserRoles.Admin)) { implicit request =>
    val body = request.body.asJson.get
    body.validate[CreateActor].fold(
      errors => {
        BadRequest(Json.obj("message" -> JsError.toJson(errors)))
      },
      actor => {
        actorRepository.create(actor.firstName, actor.surname)
        Ok(Json.obj("message" -> "Actor created"))
      }
    )
  }

  def update(id: String): Action[AnyContent] = silhouette.SecuredAction(RoleJWTAuthorization(UserRoles.Admin)).async { implicit request =>
    actorRepository.getById(id) map {
      case Some(u) =>
        val body = request.body.asJson.get
        body.validate[UpdateActor].fold(
          errors => {
            BadRequest(Json.obj("message" -> JsError.toJson(errors)))
          },
          actor => {
            actorRepository.update(id, actor.firstName.getOrElse(u.firstName), actor.surname.getOrElse(u.surname))
            Ok(Json.obj("message" -> "Actor updated"))
          }
        )
      case None => actorNotFound
    }
  }

  def delete(id: String): Action[AnyContent] = silhouette.SecuredAction(RoleJWTAuthorization(UserRoles.Admin)).async { implicit request =>
    actorRepository.getById(id) map {
      case Some(u) =>
        actorRepository.delete(id)
        Ok(Json.obj("message" -> "Actor deleted"))
      case None => actorNotFound
    }
  }
}