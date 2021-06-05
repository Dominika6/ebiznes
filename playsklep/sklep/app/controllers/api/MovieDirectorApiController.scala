package controllers.api
import com.mohiva.play.silhouette.api.Silhouette
import javax.inject.{Inject, Singleton}
import play.api.libs.json.{JsError, Json, OFormat}
import play.api.mvc._
import models.{DirectorRepository, MovieRepository}
import utils.auth.{JsonErrorHandler, JwtEnv, RoleJWTAuthorization}
import models.auth.UserRoles
import scala.concurrent.ExecutionContext

case class CreateDirector(firstName: String,
                          surname: String)

object CreateDirector {
  implicit val directorFormat: OFormat[CreateDirector] = Json.format[CreateDirector]
}


case class UpdateDirector(firstName: Option[String],
                          surname: Option[String])

object UpdateDirector {
  implicit val directorFormat: OFormat[UpdateDirector] = Json.format[UpdateDirector]
}


@Singleton
class DirectorApiController @Inject()(
                                       directorRepository: DirectorRepository,
                                       errorHandler: JsonErrorHandler,
                                       silhouette: Silhouette[JwtEnv],
                                       movieRepository: MovieRepository,
                                       cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val directorNotFound: Result = NotFound(Json.obj("message" -> "Director does not exist"))

  def getAll: Action[AnyContent] = Action.async { implicit request =>
    val directors = directorRepository.getAll
    directors.map(director => Ok(Json.toJson(director)))
  }

  def get(id: String): Action[AnyContent] = Action.async { implicit request =>
    directorRepository.getById(id) map {
      case Some(d) => Ok(Json.toJson(d))
      case None => directorNotFound
    }
  }

  def getMovies(id: String): Action[AnyContent] = Action.async { implicit request =>
    val movies = movieRepository.getForDirector(id)
    movies.map(movie => Ok(Json.toJson(movie)))
  }

  def create(): Action[AnyContent] = silhouette.SecuredAction(RoleJWTAuthorization(UserRoles.Admin)) { implicit request =>
    val body = request.body.asJson.get
    body.validate[CreateDirector].fold(
      errors => {
        BadRequest(Json.obj("message" -> JsError.toJson(errors)))
      },
      director => {
        directorRepository.create(director.firstName, director.surname)
        Ok(Json.obj("message" -> "Director created"))
      }
    )
  }

  def update(id: String): Action[AnyContent] = silhouette.SecuredAction(RoleJWTAuthorization(UserRoles.Admin)).async { implicit request =>
    directorRepository.getById(id) map {
      case Some(d) =>
        val body = request.body.asJson.get
        body.validate[UpdateDirector].fold(
          errors => {
            BadRequest(Json.obj("message" -> JsError.toJson(errors)))
          },
          director => {
            directorRepository.update(id, director.firstName.getOrElse(d.firstName), director.surname.getOrElse(d.surname))
            Ok(Json.obj("message" -> "Director updated"))
          }
        )
      case None => directorNotFound
    }
  }

  def delete(id: String): Action[AnyContent] = Action.async { implicit request =>
    directorRepository.getById(id) map {
      case Some(d) =>
        directorRepository.delete(id)
        Ok(Json.obj("message" -> "Director deleted"))
      case None => directorNotFound
    }
  }

}