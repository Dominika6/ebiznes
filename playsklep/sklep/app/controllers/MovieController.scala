package controllers
import com.mohiva.play.silhouette.api.Silhouette
import javax.inject.{Inject, Singleton}
import models._
import models.auth.{User, UserRoles}
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.format.Formats._
import play.api.mvc._
import utils.auth.{CookieEnv, RoleCookieAuthorization}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

@Singleton
class MovieController @Inject()(
                                 movieRepository: MovieRepository,
                                 directorRepository: DirectorRepository,
                                 actorRepository: ActorRepository,
                                 filmtypeRepository: FilmtypeRepository,
                                 rateRepository: RateRepository,
                                 commentRepository: CommentRepository,
                                 silhouette: Silhouette[CookieEnv],
                                 cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val createMovieForm: Form[CreateMovieForm] = Form {
    mapping(
      "title" -> nonEmptyText,
      "publicationDate" -> nonEmptyText,
      "price" -> of(doubleFormat),
      "details" -> nonEmptyText,
      "actors" -> seq(nonEmptyText),
      "directors" -> seq(nonEmptyText),
      "filmtypes" -> seq(nonEmptyText)
    )(CreateMovieForm.apply)(CreateMovieForm.unapply)
  }

  def getAll: Action[AnyContent] = silhouette.SecuredAction(RoleCookieAuthorization(UserRoles.User)).async { implicit request: Request[_]  =>
    val movies = movieRepository.getAll
    movies.map(movie => Ok(views.html.movies(movie)))
  }

  def get(movieId: String): Action[AnyContent] = silhouette.SecuredAction(RoleCookieAuthorization(UserRoles.User)).async { implicit request: Request[_]  =>
    val comments: Seq[(Comment, User)] = Await.result(commentRepository.getForMovie(movieId), Duration.Inf)
    val ratings: Seq[(Rate, User)] = Await.result(rateRepository.getForMovie(movieId), Duration.Inf)
    val actors: Seq[Actor] = Await.result(actorRepository.getForMovie(movieId), Duration.Inf)
    val filmtypes: Seq[Filmtype] = Await.result(filmtypeRepository.getForMovie(movieId), Duration.Inf)
    val directors: Seq[Director] = Await.result(directorRepository.getForMovie(movieId), Duration.Inf)

    movieRepository.getById(movieId) map {
      case Some(m) => Ok(views.html.movie(m, comments, ratings, actors, directors, filmtypes))
      case None => Redirect(routes.MovieController.getAll())
    }
  }

  def create: Action[AnyContent] = silhouette.SecuredAction(RoleCookieAuthorization(UserRoles.Admin)) { implicit request: Request[_]  =>
    val actors: Seq[Actor] = Await.result(actorRepository.getAll, Duration.Inf)
    val directors: Seq[Director] = Await.result(directorRepository.getAll, Duration.Inf)
    val filmtypes: Seq[Filmtype] = Await.result(filmtypeRepository.getAll, Duration.Inf)
    Ok(views.html.add_movie(createMovieForm, actors, directors, filmtypes))
  }

  def delete(movieId: String): Action[AnyContent] = silhouette.SecuredAction(RoleCookieAuthorization(UserRoles.Admin)).async { implicit request: Request[_]  =>
    movieRepository.delete(movieId).map(_ => Redirect(routes.MovieController.getAll()).flashing("info" -> "Film usunięty"))
  }

  def createMovieHandler: Action[AnyContent] = silhouette.SecuredAction(RoleCookieAuthorization(UserRoles.Admin)).async { implicit request: Request[_]  =>
    val errorFunction = { formWithErrors: Form[CreateMovieForm] =>
      Future {
        Redirect(routes.MovieController.create()).flashing("error" -> "Błąd podczas dodawania filmu")
      }
    }

    val successFunction = { movie: CreateMovieForm =>
      movieRepository.create(movie.title, movie.publicationDate, movie.price, movie.details, movie.actors, movie.directors, movie.filmtypes).map { _ =>
        Redirect(routes.MovieController.getAll()).flashing("success" -> "Film dodany")
      }
    }
    createMovieForm.bindFromRequest.fold(errorFunction, successFunction)
  }

  def update(movieId: String): Action[AnyContent] = silhouette.SecuredAction(RoleCookieAuthorization(UserRoles.Admin)) { implicit request: Request[_]  =>
    val movie: Movie = Await.result(movieRepository.getById(movieId), Duration.Inf).get

    val actors: Seq[Actor] = Await.result(actorRepository.getAll, Duration.Inf)
    val selectedActors: Seq[String] = Await.result(actorRepository.getForMovie(movieId), Duration.Inf).map(_.actorId)

    val directors: Seq[Director] = Await.result(directorRepository.getAll, Duration.Inf)
    val selectedDirectors: Seq[String] = Await.result(directorRepository.getForMovie(movieId), Duration.Inf).map(_.directorId)

    val filmtypes: Seq[Filmtype] = Await.result(filmtypeRepository.getAll, Duration.Inf)
    val selectedFilmtypes: Seq[String] = Await.result(filmtypeRepository.getForMovie(movieId), Duration.Inf).map(_.filmtypeId)

    val updateForm = createMovieForm.fill(
      CreateMovieForm(movie.title, movie.publicationDate, movie.price, movie.details, selectedActors, selectedDirectors, selectedFilmtypes)
    )

    Ok(views.html.update_movie(
      movieId, updateForm,
      selectedActors, actors,
      selectedDirectors, directors,
      selectedFilmtypes, filmtypes))
  }

  def updateMovieHandler(movieId: String): Action[AnyContent] = silhouette.SecuredAction(RoleCookieAuthorization(UserRoles.Admin)).async { implicit request: Request[_]  =>
    val errorFunction = { formWithErrors: Form[CreateMovieForm] =>
      Future {
        Redirect(routes.MovieController.update(movieId)).flashing("error" -> "Błąd podczas edycji filmu")
      }
    }

    val successFunction = { movie: CreateMovieForm =>
      movieRepository.update(movieId, movie.title, movie.publicationDate, movie.price, movie.details, movie.actors, movie.directors, movie.filmtypes).map { _ =>
        Redirect(routes.MovieController.getAll()).flashing("success" -> "Film zmodyfikowany")
      }
    }
    createMovieForm.bindFromRequest.fold(errorFunction, successFunction)
  }
}

case class CreateMovieForm(title: String,
                           publicationDate: String,
                           price: Double,
                           details: String,
                           actors: Seq[String],
                           directors: Seq[String],
                           filmtypes: Seq[String]
                          )