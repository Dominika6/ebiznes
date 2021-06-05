package controllers.api
import com.mohiva.play.silhouette.api.Silhouette
import models.auth.{User, UserRoles}
import javax.inject.{Inject, Singleton}
import models.{Movie, Rate}
import play.api.libs.json.{JsError, JsValue, Json, OFormat}
import play.api.mvc._
import models.{MovieRepository, RateRepository}
import repoauth.UserService
import utils.auth.{JsonErrorHandler, JwtEnv, RoleJWTAuthorization}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class CreateRate(value: Int,
                        movie: String)

object CreateRate {
  implicit val rateFormat: OFormat[CreateRate] = Json.format[CreateRate]
}


@Singleton
class RateApiController @Inject()(rateRepository: RateRepository,
                                  userRepository: UserService,
                                  movieRepository: MovieRepository,
                                  silhouette: Silhouette[JwtEnv],
                                  errorHandler: JsonErrorHandler,
                                  cc: MessagesControllerComponents
                                 )(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val rateNotFound: Result = NotFound(Json.obj("message" -> "Rate does not exist"))

  def getAll: Action[AnyContent] = Action.async { implicit request =>
    val rates: Future[Seq[(Rate, Movie, User)]] = rateRepository.getAllWithMovieAndUser
    rates.map(rate => {
      val newRate = rate.map {
        case (r, m, u) => Json.obj("rateId" -> r.rateId, "result" -> r.result, "movie" -> m, "user" -> u)
      }
      Ok(Json.toJson(newRate))
    })
  }

  def get(id: String): Action[AnyContent] = Action.async { implicit request =>
    rateRepository.getByIdWithMovieAndUser(id) map {
      case Some(r) => Ok(Json.toJson(Json.obj("movieId" -> r._1.movieId, "result" -> r._1.result, "movie" -> r._2, "user" -> r._3)))
      case None => rateNotFound
    }
  }

  def create(): Action[JsValue] = silhouette.SecuredAction(errorHandler).async(parse.json) { implicit request =>
    val body = request.body
    body.validate[CreateRate].fold(
      errors => {
        Future.successful(BadRequest(Json.obj("message" -> JsError.toJson(errors))))
      },
      rate => {
        val userExist: Boolean = Await.result(userRepository.isExist(request.identity.userId), Duration.Inf)
        if (!userExist) {
          Future.successful(BadRequest(Json.obj("message" -> "User does not exist")))
        } else {
          val movieExist: Boolean = Await.result(movieRepository.isExist(rate.movie), Duration.Inf)
          if (!movieExist) {
            Future.successful(BadRequest(Json.obj("message" -> "Movie does not exist")))
          } else {
            rateRepository.create(rate.value, request.identity.userId, rate.movie)
            Future.successful(Ok(Json.obj("message" -> "Rate created")))
          }
        }
      }
    )
  }

  def delete(id: String): Action[AnyContent] = silhouette.SecuredAction(RoleJWTAuthorization(UserRoles.Admin)).async { implicit request =>
    rateRepository.getById(id) map {
      case Some(r) =>
        rateRepository.delete(id)
        Ok(Json.obj("message" -> "Rate deleted"))
      case None => rateNotFound
    }
  }
}