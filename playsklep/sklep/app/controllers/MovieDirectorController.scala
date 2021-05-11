package controllers

import javax.inject.{Inject, Singleton}
import models.{Director, Movie}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import models.{DirectorRepository, MovieRepository}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}


@Singleton
class DirectorController @Inject()(directorRepository: DirectorRepository, movieRepository: MovieRepository, cc: MessagesControllerComponents,
                                   )(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val createDirectorForm: Form[CreateDirectorForm] = Form {
    mapping(
      "firstName" -> nonEmptyText,
      "surname" -> nonEmptyText
    )(CreateDirectorForm.apply)(CreateDirectorForm.unapply)
  }

  def getAll : Action[AnyContent] = Action.async { implicit request: Request[_]  =>
    val directors = directorRepository.getAll()
    directors.map(director => Ok(views.html.director.directors(director)))
  }

  def get(directorId: String) : Action[AnyContent] = Action.async { implicit request: Request[_]  =>
    val movies: Seq[Movie] = Await.result(movieRepository.getForDirector(directorId), Duration.Inf)

    directorRepository.getById(directorId) map {
      case Some(d) => Ok(views.html.director.director(d, movies))
      case None => Redirect(routes.DirectorController.getAll())
    }
  }

  def create : Action[AnyContent] = Action.async { implicit request: Request[_]  =>
    Ok(views.html.director.add_director(createDirectorForm))
  }

  def createDirectorHandler: Action[AnyContent] = Action.async { implicit request: Request[_]  =>
    val errorFunction = { formWithErrors: Form[CreateDirectorForm] =>
      Future {
        Redirect(routes.DirectorController.create()).flashing("error" -> "Błąd podczas dodawania reżysera")
      }
    }

    val successFunction = { director: CreateDirectorForm =>
      directorRepository.create(director.firstName, director.surname).map { _ =>
        Redirect(routes.DirectorController.getAll()).flashing("success" -> "Reżyser dodany")
      };
    }
    createDirectorForm.bindFromRequest.fold(errorFunction, successFunction)
  }

  def delete(directorId: String) : Action[AnyContent] = Action.async { implicit request: Request[_]  =>
    directorRepository.delete(directorId).map(_ => Redirect(routes.DirectorController.getAll()).flashing("info" -> "Reżyser usunięty"))
  }

  def update(directorId: String) : Action[AnyContent] = Action.async { implicit request: Request[_]  =>
    val director: Director = Await.result(directorRepository.getById(directorId), Duration.Inf).get
    val updateForm = createDirectorForm.fill(CreateDirectorForm(director.firstName, director.surname))
    Ok(views.html.director.update_director(directorId, updateForm))
  }

  def updateDirectorHandler(directorId: String): Action[AnyContent] = Action.async { implicit request: Request[_]  =>
    val errorFunction = { formWithErrors: Form[CreateDirectorForm] =>
      Future {
        Redirect(routes.DirectorController.update(directorId)).flashing("error" -> "Błąd podczas edycji reżysera")
      }
    }

    val successFunction = { director: CreateDirectorForm =>
      directorRepository.update(directorId, director.firstName, director.surname).map { _ =>
        Redirect(routes.DirectorController.getAll()).flashing("success" -> "Reżyser zmodyfikowany")
      };
    }
    createDirectorForm.bindFromRequest.fold(errorFunction, successFunction)
  }
}

case class CreateDirectorForm(firstName: String,
                              surname: String)