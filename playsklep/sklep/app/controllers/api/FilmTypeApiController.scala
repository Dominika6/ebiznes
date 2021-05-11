package controllers.api

import javax.inject.{Inject, Singleton}
import play.api.libs.json.{JsError, Json}
import play.api.mvc._
import models.{FilmtypeRepository, MovieRepository}

import scala.concurrent.ExecutionContext

case class CreateFilmtype(name: String)

object CreateFilmtype {
  implicit val filmtypeFormat = Json.format[CreateFilmtype]
}

case class UpdateFilmtype(name: Option[String])

object UpdateFilmtype {
  implicit val filmtypeFormat = Json.format[UpdateFilmtype]
}

@Singleton
class FilmtypeApiController @Inject()(
                                    filmtypeRepository: FilmtypeRepository,
                                    movieRepository: MovieRepository,
                                    errorHandler: JsonErrorHandler,
                                    cc: MessagesControllerComponents
                                  )(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val filmtypeNotFound = NotFound(Json.obj("message" -> "Filmtype does not exist"))

  def getAll: Action[AnyContent] = Action.async { implicit request =>
    val filmtypes = filmtypeRepository.getAll()
    filmtypes.map(filmtype => Ok(Json.toJson(filmtype)))
  }

  def getMovies(id: String): Action[AnyContent] = Action.async { implicit request =>
    val movies = movieRepository.getForFilmtype(id)
    movies.map(movie => Ok(Json.toJson(movie)))
  }

  def get(id: String) = Action.async { implicit request =>
    filmtypeRepository.getById(id) map {
      case Some(g) => Ok(Json.toJson(g))
      case None => filmtypeNotFound
    }
  }

  def create() : Action[AnyContent] = Action.async { implicit request =>
    val body = request.body.asJson.get
    body.validate[CreateFilmtype].fold(
      errors => {
        BadRequest(Json.obj("message" -> JsError.toJson(errors)))
      },
      filmtype => {
        filmtypeRepository.create(filmtype.name)
        Ok(Json.obj("message" -> "Filmtype created"))
      }
    )
  }

  def update(id: String) : Action[AnyContent] = Action.async { implicit request =>
    filmtypeRepository.getById(id) map {
      case Some(g) => {
        val body = request.body
        body.validate[UpdateFilmtype].fold(
          errors => {
            BadRequest(Json.obj("message" -> JsError.toJson(errors)))
          },
          filmtype => {
            filmtypeRepository.update(id, filmtype.name.getOrElse(g.name))
            Ok(Json.obj("message" -> "Filmtype updated"))
          }
        )
      }
      case None => filmtypeNotFound
    }
  }

  def delete(id: String) : Action[AnyContent] = Action.async { implicit request =>
    filmtypeRepository.getById(id) map {
      case Some(g) => {
        filmtypeRepository.delete(id)
        Ok(Json.obj("message" -> "Filmtype deleted"))
      }
      case None => filmtypeNotFound
    }
  }

}