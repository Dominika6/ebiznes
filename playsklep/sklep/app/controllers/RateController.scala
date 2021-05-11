package controllers

import javax.inject.{Inject, Singleton}
import models.{Movie, Rate}
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.format.Formats._
import play.api.mvc._
import models.{MovieRepository, RateRepository}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

@Singleton
class RateController @Inject()(rateRepository: RateRepository, userRepository: UserService, movieRepository: MovieRepository, cc: MessagesControllerComponents,
                                 )(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val createRateForm: Form[CreateRateForm] = Form {
    mapping(
      "result" -> of(intFormat),
      "userId" -> nonEmptyText,
      "movieId" -> nonEmptyText
    )(CreateRateForm.apply)(CreateRateForm.unapply)
  }

  def getAll : Action[AnyContent] = Action.async { implicit request: Request[_]  =>
    val rates = rateRepository.getAllWithMovieAndUser()
    rates.map(rate => Ok(views.html.rate.rates(rate)))
  }

  def create : Action[AnyContent] = Action.async { implicit request: Request[_]  =>
    val users: Seq[User] = Await.result(userRepository.getAll(), Duration.Inf)
    val movies: Seq[Movie] = Await.result(movieRepository.getAll(), Duration.Inf);
    Ok(views.html.rate.add_rate(createRateForm, users, movies))
  }

  def createRateHandler: Action[AnyContent] = Action.async { implicit request: Request[_]  =>
    val errorFunction = { formWithErrors: Form[CreateRateForm] =>
      Future {
        Redirect(routes.RateController.create()).flashing("error" -> "Błąd podczas dodawania oceny")
      }
    }

    val successFunction = { rate: CreateRateForm =>
      rateRepository.create(rate.result, rate.userId, rate.movieId).map { _ =>
        Redirect(routes.RateController.getAll()).flashing("success" -> "Ocena dodana")
      };
    }
    createRateForm.bindFromRequest.fold(errorFunction, successFunction)
  }

  def delete(rateId: String) : Action[AnyContent] = Action.async { implicit request: Request[_]  =>
    rateRepository.delete(rateId).map(_ => Redirect(routes.RateController.getAll()).flashing("info" -> "Ocena filmu została usunięta"))
  }


  def update(rateId: String) : Action[AnyContent] = Action.async { implicit request: Request[_]  =>
    val users: Seq[User] = Await.result(userRepository.getAll(), Duration.Inf)
    val movies: Seq[Movie] = Await.result(movieRepository.getAll(), Duration.Inf);
    val rate: Rate = Await.result(rateRepository.getById(rateId), Duration.Inf).get
    val updateForm = createRateForm.fill(CreateRateForm(rate.result, rate.user, rate.movie))
    Ok(views.html.rate.update_rate(rateId, updateForm, users, movies))
  }

  def updateRateHandler(rateId: String): : Action[AnyContent] = Action.async { implicit request: Request[_]  =>
    val errorFunction = { formWithErrors: Form[CreateRateForm] =>
      Future {
        Redirect(routes.RateController.update(rateId)).flashing("error" -> "Błąd podczas edycji oceny filmu")
      }
    }

    val successFunction = { rate: CreateRateForm =>
      rateRepository.update(rateId, rate.result, rate.userId, rate.movieId).map { _ =>
        Redirect(routes.RateController.getAll()).flashing("success" -> "Ocena zmodyfikowana")
      };
    }
    createRateForm.bindFromRequest.fold(errorFunction, successFunction)
  }

}

case class CreateRateForm(result: Int,
                            userId: String,
                            movieId: String)