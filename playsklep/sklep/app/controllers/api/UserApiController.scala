package controllers.api


import javax.inject.{Inject, Singleton}
import models.{Order, Pay}
import play.api.libs.json.{JsError, Json}
import play.api.mvc._
import models._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class EditUser(firstName: String,
                    surname: String)

object EditUser {
  implicit val userFormat = Json.format[EditUser]
}

@Singleton
class UserApiController @Inject()(movieRepository: MovieRepository,
                                  actorRepository: ActorRepository,
                                  directorRepository: DirectorRepository,
                                  filmtypeRepository: FilmtypeRepository,
                                  commentRepository: CommentRepository,
                                  orderRepository: OrderRepository,
                                  userRepository: UserService,
                                  rateRepository: RateRepository,
                                  cc: MessagesControllerComponents,
                                  errorHandler: JsonErrorHandler
                                 )(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  def getMovies : Action[AnyContent] = Action.async { implicit request =>
    val movies = movieRepository.getForUser(request.identity.id)
    movies.map(movie => Ok(Json.toJson(movie)))
  }

  def getOrders : Action[AnyContent] = Action.async { implicit request =>
    val orders: Future[Seq[(Order, Pay)]] = orderRepository.getForUser(request.identity.id);
    orders.map(order => {
      val newOrder = order.map {
        case (o, p) => {
          val orderItems = Await.result(orderRepository.getOrderItemsWithMovie(o.id), Duration.Inf)
          val orderItemsJson = orderItems.map {
            case (oi, m) => Json.obj("price" -> oi.price, "movie" -> m)
          }
          val value: Double = Await.result(orderRepository.getOrderValue(o.id), Duration.Inf)
          Json.obj("id" -> o.id, "pay" -> p, "items" -> orderItemsJson, "value" -> value)
        }
      }
      Ok(Json.toJson(newOrder))
    });
  }

  def updateUser : Action[AnyContent] = Action.async { implicit request =>
    val body = request.body.asJson.get
    val _user = Await.result(userRepository.getById(request.identity.id), Duration.Inf).get;
    body.validate[EditUser].fold(
      errors => {
        Future.successful(BadRequest(Json.obj("message" -> JsError.toJson(errors))))
      },
      user => {
        userRepository.update(request.identity.id, user.firstName, user.surname, _user.email, _user.role)
        Future.successful(Ok(Json.obj("message" -> "User edited")))
      }
    )
  }
}