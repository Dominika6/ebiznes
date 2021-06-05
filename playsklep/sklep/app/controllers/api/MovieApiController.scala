package controllers.api
import com.mohiva.play.silhouette.api.Silhouette

import javax.inject.{Inject, Singleton}
import play.api.libs.json.{JsError, JsValue, Json, OFormat}
import play.api.mvc._
import models._
import models.auth.UserRoles
import utils.auth.{JwtEnv, RoleJWTAuthorization}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}

case class CreateMovie(title: String,
                       publicationDate: String,
                       price: Double,
                       details: String,
                       actors: Seq[String],
                       directors: Seq[String],
                       filmtypes: Seq[String])

object CreateMovie {
  implicit val movieFormat: OFormat[CreateMovie] = Json.format[CreateMovie]
}


case class UpdateMovie(title: Option[String],
                       publicationDate: Option[String],
                       price: Option[Double],
                       details: Option[String],
                       actors: Option[Seq[String]],
                       directors: Option[Seq[String]],
                       filmtypes: Option[Seq[String]])

object UpdateMovie {
  implicit val movieFormat: OFormat[UpdateMovie] = Json.format[UpdateMovie]
}


@Singleton
class MovieApiController @Inject()(movieRepository: MovieRepository,
                                   actorRepository: ActorRepository,
                                   directorRepository: DirectorRepository,
                                   filmtypeRepository: FilmtypeRepository,
                                   commentRepository: CommentRepository,
                                   rateRepository: RateRepository,
                                   cc: MessagesControllerComponents,
                                   silhouette: Silhouette[JwtEnv]
                                  )(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val movieNotFound: Result = NotFound(Json.obj("message" -> "Movie does not exist"))

  def getAll: Action[AnyContent] = Action.async { implicit request =>
    val movies = movieRepository.getAll
    movies.map(movie => Ok(Json.toJson(movie)))
  }

  def get(id: String): Action[AnyContent] = Action.async { implicit request =>
    movieRepository.getById(id) map {
      case Some(m) => Ok(Json.toJson(m))
      case None => movieNotFound
    }
  }

  def getActors(id: String): Action[AnyContent] = Action.async { implicit request =>
    val actors = actorRepository.getForMovie(id)
    actors.map(actor => Ok(Json.toJson(actor)))
  }

  def getDirectors(id: String): Action[AnyContent] = Action.async { implicit request =>
    val directors = directorRepository.getForMovie(id)
    directors.map(director => Ok(Json.toJson(director)))
  }

  def getFilmtypes(id: String): Action[AnyContent] = Action.async { implicit request =>
    val filmtypes = filmtypeRepository.getForMovie(id)
    filmtypes.map(filmtype => Ok(Json.toJson(filmtype)))
  }

  def getComments(id: String): Action[AnyContent] = Action.async { implicit request =>
    val comments = commentRepository.getForMovie(id)
    comments.map(comment => {
      val newComment = comment.map {
        case (c, u) => Json.obj("commentId" -> c.commentId, "content" -> c.comment, "user" -> u)
      }
      Ok(Json.toJson(newComment))
    })
  }

  def getRates(id: String): Action[AnyContent] = Action.async { implicit request =>
    val rates = rateRepository.getForMovie(id)
    rates.map(rate => {
      val newRate = rate.map {
        case (r, u) => Json.obj("rateId" -> r.rateId, "result" -> r.result, "user" -> u)
      }
      Ok(Json.toJson(newRate))
    })
  }

  def create(): Action[AnyContent] = silhouette.SecuredAction(RoleJWTAuthorization(UserRoles.Admin)) { request =>
    val body = request.body.asJson.get

    body.validate[CreateMovie].fold(
      errors => {
        BadRequest(Json.obj("message" -> JsError.toJson(errors)))
      },
      movie => {
        var invalidActors: Seq[String] = Seq()
        for (a <- movie.actors) {
          if (!(Await.result(actorRepository.isExist(a), Duration.Inf))) {
            invalidActors = invalidActors :+ a
          }
        }

        var invalidDirectors: Seq[String] = Seq()
        for (d <- movie.directors) {
          if (!(Await.result(directorRepository.isExist(d), Duration.Inf))) {
            invalidDirectors = invalidDirectors :+ d
          }
        }

        var invalidFilmtypes: Seq[String] = Seq()
        for (g <- movie.filmtypes) {
          if (!(Await.result(filmtypeRepository.isExist(g), Duration.Inf))) {
            invalidFilmtypes = invalidFilmtypes :+ g
          }
        }
        val valid: Boolean = invalidActors.isEmpty && invalidDirectors.isEmpty && invalidFilmtypes.isEmpty
        if (!valid) {
          BadRequest(Json.obj("invalid_actors" -> invalidActors, "invalid_directors" -> invalidDirectors, "invalid_filmtypes" -> invalidFilmtypes))
        } else {
          movieRepository.create(movie.title, movie.publicationDate, movie.price, movie.details, movie.actors, movie.directors, movie.filmtypes)
          Ok(Json.obj("message" -> "Movie created"))
        }
      }
    )
  }

  def update(id: String): Action[JsValue] = silhouette.SecuredAction(RoleJWTAuthorization(UserRoles.Admin)).async(parse.json) { implicit request =>
    movieRepository.getById(id) map {
      case Some(m) =>
        val body = request.body
        body.validate[UpdateMovie].fold(
          errors => {
            BadRequest(Json.obj("message" -> JsError.toJson(errors)))
          },
          movie => {
            var invalidActors: Seq[String] = Seq()
            if (movie.actors.nonEmpty) {
              for (a <- movie.actors.get) {
                if (!(Await.result(actorRepository.isExist(a), Duration.Inf))) {
                  invalidActors = invalidActors :+ a
                }
              }
            }


            var invalidDirectors: Seq[String] = Seq()
            if (movie.directors.nonEmpty) {
              for (d <- movie.directors.get) {
                if (!(Await.result(directorRepository.isExist(d), Duration.Inf))) {
                  invalidDirectors = invalidDirectors :+ d
                }
              }
            }

            var invalidFilmtypes: Seq[String] = Seq()
            if (movie.filmtypes.nonEmpty) {
              for (g <- movie.filmtypes.get) {
                if (!(Await.result(filmtypeRepository.isExist(g), Duration.Inf))) {
                  invalidFilmtypes = invalidFilmtypes :+ g
                }
              }
            }

            val valid: Boolean = invalidActors.isEmpty && invalidDirectors.isEmpty && invalidFilmtypes.isEmpty
            if (!valid) {
              BadRequest(Json.obj("invalid_actors" -> invalidActors, "invalid_directors" -> invalidDirectors, "invalidFilmtypes" -> invalidFilmtypes))
            } else {
              val currentActors: Seq[String] = Await.result(actorRepository.getForMovie(id), Duration.Inf).map(_.actorId)
              val currentDirectors: Seq[String] = Await.result(directorRepository.getForMovie(id), Duration.Inf).map(_.directorId)
              val currentFilmtypes: Seq[String] = Await.result(filmtypeRepository.getForMovie(id), Duration.Inf).map(_.filmtypeId)

              movieRepository.update(id,
                movie.title.getOrElse(m.title),
                movie.publicationDate.getOrElse(m.publicationDate),
                movie.price.getOrElse(m.price),
                movie.details.getOrElse(m.details),
                movie.actors.getOrElse(currentActors),
                movie.directors.getOrElse(currentDirectors),
                movie.filmtypes.getOrElse(currentFilmtypes)
              )
              Ok(Json.obj("message" -> "Movie updated"))
            }
          }
        )
      case None => movieNotFound
    }
  }

  def delete(id: String): Action[AnyContent] = silhouette.SecuredAction(RoleJWTAuthorization(UserRoles.Admin)).async { implicit request =>
    movieRepository.getById(id) map {
      case Some(m) =>
        movieRepository.delete(id)
        Ok(Json.obj("message" -> "Movie deleted"))
      case None => movieNotFound
    }
  }
}