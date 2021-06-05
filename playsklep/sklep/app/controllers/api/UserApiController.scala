package controllers.api
import com.mohiva.play.silhouette.api.Silhouette
import javax.inject.{Inject, Singleton}
import models.{Order, Pay}
import play.api.libs.json.{JsError, Json, OFormat}
import play.api.mvc._
import models._
import repoauth.UserService
import utils.auth.{JsonErrorHandler, JwtEnv}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class EditUser(firstName: String,
                    surname: String)

object EditUser {
  implicit val userFormat: OFormat[EditUser] = Json.format[EditUser]
}

@Singleton
class UserApiController @Inject()(movieRepository: MovieRepository,
                                  orderRepository: OrderRepository,
                                  userRepository: UserService,
                                  cc: MessagesControllerComponents,
                                  errorHandler: JsonErrorHandler,
                                  silhouette: Silhouette[JwtEnv]
                                 )(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  def getMovies: Action[AnyContent] = silhouette.SecuredAction(errorHandler).async { implicit request =>
    val movies = movieRepository.getForUser(request.identity.userId)
    movies.map(movie => Ok(Json.toJson(movie)))
  }

  def getOrders: Action[AnyContent] = silhouette.SecuredAction(errorHandler).async { implicit request =>
    val orders: Future[Seq[(Order, Pay)]] = orderRepository.getForUser(request.identity.userId);
    orders.map(order => {
      val newOrder = order.map {
        case (o, p) =>
          val orderItems = Await.result(orderRepository.getOrderItemsWithMovie(o.orderId), Duration.Inf)
          val orderItemsJson = orderItems.map {
            case (oi, m) => Json.obj("price" -> oi.price, "movie" -> m)
          }
          val value: Double = Await.result(orderRepository.getOrderValue(o.orderId), Duration.Inf)
          Json.obj("orderId" -> o.orderId, "pay" -> p, "items" -> orderItemsJson, "value" -> value)
      }
      Ok(Json.toJson(newOrder))
    });
  }

  def updateUser(): Action[AnyContent] = silhouette.SecuredAction(errorHandler).async { implicit request =>
    val body = request.body.asJson.get
    val _user = Await.result(userRepository.getById(request.identity.userId), Duration.Inf).get;
    body.validate[EditUser].fold(
      errors => {
        Future.successful(BadRequest(Json.obj("message" -> JsError.toJson(errors))))
      },
      user => {
        userRepository.update(request.identity.userId, user.firstName, user.surname, _user.email, _user.role)
        Future.successful(Ok(Json.obj("message" -> "User edited")))
      }
    )
  }
}