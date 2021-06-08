package controllers
import com.mohiva.play.silhouette.api.Silhouette
import models.auth.UserRoles
import javax.inject.{Inject, Singleton}
import models.{Filmtype, Movie}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import models.{FilmtypeRepository, MovieRepository}
import utils.auth.{CookieEnv, RoleCookieAuthorization}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

@Singleton
class FilmtypeController @Inject()(filmtypeRepository: FilmtypeRepository, movieRepository: MovieRepository, cc: MessagesControllerComponents,
                                   silhouette: Silhouette[CookieEnv])(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {
  val createFilmtypeForm: Form[CreateFilmtypeForm] = Form {
    mapping(
      "filmtype" -> nonEmptyText
    )(CreateFilmtypeForm.apply)(CreateFilmtypeForm.unapply)
  }

  def getAll: Action[AnyContent] = silhouette.SecuredAction(RoleCookieAuthorization(UserRoles.User)).async { implicit request: Request[_]  =>
    val filmtypes = filmtypeRepository.getAll
    filmtypes.map(filmtype => Ok(views.html.filmtypes(filmtype)))
  }

  def get(filmtypeId: String): Action[AnyContent] = silhouette.SecuredAction(RoleCookieAuthorization(UserRoles.User)).async { implicit request: Request[_]  =>
    val movies: Seq[Movie] = Await.result(movieRepository.getForFilmtype(filmtypeId), Duration.Inf)

    filmtypeRepository.getById(filmtypeId) map {
      case Some(g) => Ok(views.html.filmtype(g, movies))
      case None => Redirect(routes.FilmtypeController.getAll)
    }
  }

  def create: Action[AnyContent] = silhouette.SecuredAction(RoleCookieAuthorization(UserRoles.Admin)) { implicit request: Request[_] =>
    Ok(views.html.add_filmtype(createFilmtypeForm))
  }


  def createFilmtypeHandler: Action[AnyContent] = silhouette.SecuredAction(RoleCookieAuthorization(UserRoles.Admin)).async { implicit request: Request[_]  =>
    val errorFunction = { formWithErrors: Form[CreateFilmtypeForm] =>
      Future {
        Redirect(routes.FilmtypeController.create).flashing("error" -> "Błąd podczas dodawania gatunku!")
      }
    }

    val successFunction = { filmtype: CreateFilmtypeForm =>
      filmtypeRepository.create(filmtype.name).map { _ =>
        Redirect(routes.FilmtypeController.getAll).flashing("success" -> "Gatunek dodany!")
      };
    }
    createFilmtypeForm.bindFromRequest.fold(errorFunction, successFunction)
  }

  def delete(filmtypeId: String): Action[AnyContent] = silhouette.SecuredAction(RoleCookieAuthorization(UserRoles.Admin)).async { implicit request: Request[_]  =>
    filmtypeRepository.delete(filmtypeId).map(_ => Redirect(routes.FilmtypeController.getAll).flashing("info" -> "Gatunek usunięty!"))
  }

  def update(filmtypeId: String): Action[AnyContent] = silhouette.SecuredAction(RoleCookieAuthorization(UserRoles.Admin)) { implicit request: Request[_]  =>
    val filmtype: Filmtype = Await.result(filmtypeRepository.getById(filmtypeId), Duration.Inf).get
    val updateForm = createFilmtypeForm.fill(CreateFilmtypeForm(filmtype.filmtype))
    Ok(views.html.update_filmtype(filmtypeId, updateForm))
  }

  def updateFilmtypeHandler(filmtypeId: String): Action[AnyContent] = silhouette.SecuredAction(RoleCookieAuthorization(UserRoles.Admin)).async { implicit request: Request[_]  =>
    val errorFunction = { formWithErrors: Form[CreateFilmtypeForm] =>
      Future {
        Redirect(routes.FilmtypeController.update(filmtypeId)).flashing("error" -> "Błąd podczas edycji gatunku!")
      }
    }

    val successFunction = { filmtype: CreateFilmtypeForm =>
      filmtypeRepository.update(filmtypeId, filmtype.name).map { _ =>
        Redirect(routes.FilmtypeController.getAll).flashing("success" -> "Gatunek zmodyfikowany!")
      };
    }
    createFilmtypeForm.bindFromRequest.fold(errorFunction, successFunction)
  }

}

case class CreateFilmtypeForm(name: String)