package controllers.api

import javax.inject.{Inject, Singleton}
import models.{User, Movie, Rate}
import play.api.libs.json.{JsError, Json}
import play.api.mvc._
import models.{MovieRepository, RateRepository}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class CreateRate(value: Int,
                        movie: String)

object CreateRate {
  implicit val rateFormat = Json.format[CreateRate]
}


@Singleton
class RateApiController @Inject()(rateRepository: RateRepository, userRepository: UserService, movieRepository: MovieRepository,
                                    errorHandler: JsonErrorHandler, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val rateNotFound = NotFound(Json.obj("message" -> "Rate does not exist"))

  def getAll: Action[AnyContent] = Action.async { implicit request =>
    val rates: Future[Seq[(Rate, Movie, User)]] = rateRepository.getAllWithMovieAndUser()
    rates.map(rate => {
      val newRate = rate.map {
        case (r, m, u) => Json.obj("id" -> r.id, "value" -> r.value, "movie" -> m, "user" -> u)
      }
      Ok(Json.toJson(newRate))
    })
  }

  def get(id: String) = Action.async { implicit request =>
    rateRepository.getByIdWithMovieAndUser(id) map {
      case Some(r) => Ok(Json.toJson(Json.obj("id" -> r._1.id, "value" -> r._1.value, "movie" -> r._2, "user" -> r._3)))
      case None => rateNotFound
    }
  }

  def create() : Action[AnyContent] = Action.async { implicit request =>
    val body = request.body
    body.validate[CreateRate].fold(
      errors => {
        Future.successful(BadRequest(Json.obj("message" -> JsError.toJson(errors))))
      },
      rate => {
        val userExist: Boolean = Await.result(userRepository.isExist(request.identity.id), Duration.Inf)
        if (!userExist) {
          Future.successful(BadRequest(Json.obj("message" -> "User does not exist")))
        } else {
          val movieExist: Boolean = Await.result(movieRepository.isExist(rate.movie), Duration.Inf)
          if (!movieExist) {
            Future.successful(BadRequest(Json.obj("message" -> "Movie does not exist")))
          } else {
            rateRepository.create(rate.value, request.identity.id, rate.movie)
            Future.successful(Ok(Json.obj("message" -> "Rate created")))
          }
        }
      }
    )
  }

  def delete(id: String) : Action[AnyContent] = Action.async { implicit request =>
    rateRepository.getById(id) map {
      case Some(r) => {
        rateRepository.delete(id)
        Ok(Json.obj("message" -> "Rate deleted"))
      }
      case None => rateNotFound
    }
  }
}