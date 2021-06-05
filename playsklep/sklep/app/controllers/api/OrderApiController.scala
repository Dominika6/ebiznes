package controllers.api
import com.mohiva.play.silhouette.api.Silhouette
import javax.inject.{Inject, Singleton}
import models.{Order, Pay}
import play.api.libs.json.{JsError, JsValue, Json, OFormat}
import play.api.mvc._
import models._
import models.auth.{User, UserRoles}
import repoauth.UserService
import utils.auth.{JsonErrorHandler, JwtEnv, RoleJWTAuthorization}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class CreateOrder(pay: String,
                       movies: Seq[String])

object CreateOrder {
  implicit val orderFormat: OFormat[CreateOrder] = Json.format[CreateOrder]
}

case class UpdateOrder(user: Option[String],
                       pay: Option[String],
                       movies: Option[Seq[String]])

object UpdateOrder {
  implicit val orderFormat: OFormat[UpdateOrder] = Json.format[UpdateOrder]
}

@Singleton
class OrderApiController @Inject()(orderRepository: OrderRepository,
                                   movieRepository: MovieRepository,
                                   userRepository: UserService,
                                   payRepository: PayRepository,
                                   cc: MessagesControllerComponents,
                                   errorHandler: JsonErrorHandler,
                                   silhouette: Silhouette[JwtEnv]
                                  )(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val orderNotFound: Result = NotFound(Json.obj("message" -> "Order does not exist"))

  def getAll: Action[AnyContent] = silhouette.SecuredAction(RoleJWTAuthorization(UserRoles.Admin)).async { implicit request =>
    val orders: Future[Seq[(Order, Pay, User, Double)]] = orderRepository.getAllWithPayAndUser
    orders.map(orders => {
      val newOrder = orders.map {
        case (o, p, u, v) => Json.obj("id" -> o.orderId, "pay" -> p, "user" -> u, "value" -> v)
      }
      Ok(Json.toJson(newOrder))
    })
  }

  def get(id: String): Action[AnyContent] = silhouette.SecuredAction(RoleJWTAuthorization(UserRoles.Admin)).async { implicit request =>
    orderRepository.getByIdWithUserAndPay(id) map {
      case Some(order) =>
        val orderItems = Await.result(orderRepository.getOrderItemsWithMovie(id), Duration.Inf)
        val orderItemsJson = orderItems.map {
          case (oi, m) => Json.obj("price" -> oi.price, "movie" -> m)
        }
        val value: Double = Await.result(orderRepository.getOrderValue(id), Duration.Inf)
        Ok(Json.obj("orderId" -> order._1.orderId, "pay" -> order._2, "user" -> order._3, "value" -> value, "items" -> orderItemsJson))
      case None => orderNotFound
    }
  }

  def create(): Action[JsValue] = silhouette.SecuredAction(errorHandler).async(parse.json) { request =>
    val body = request.body

    body.validate[CreateOrder].fold(
      errors => {
        Future.successful(BadRequest(Json.obj("message" -> JsError.toJson(errors))))
      },
      order => {
        var invalidMovies: Seq[String] = Seq()
        for (m <- order.movies) {
          if (!(Await.result(movieRepository.isExist(m), Duration.Inf))) {
            invalidMovies = invalidMovies :+ m
          }
        }
        val isUserExist: Boolean = Await.result(userRepository.isExist(request.identity.userId), Duration.Inf)
        val isPayExist: Boolean = Await.result(payRepository.isExist(order.pay), Duration.Inf)

        val valid: Boolean = invalidMovies.isEmpty && isUserExist && isPayExist
        if (!valid) {
          if (!isUserExist) {
            Future.successful(BadRequest(Json.obj("message" -> "User does not exist")))
          } else if (!isPayExist) {
            Future.successful(BadRequest(Json.obj("message" -> "Pay does not exist")))
          } else {
            Future.successful(BadRequest(Json.obj("invalid_movies" -> invalidMovies)))
          }
        } else {
          orderRepository.create(request.identity.userId, order.pay, order.movies)
          Future.successful(Ok(Json.obj("message" -> "Order created")))
        }
      }
    )
  }


  def update(id: String): Action[JsValue] = silhouette.SecuredAction(RoleJWTAuthorization(UserRoles.Admin)).async(parse.json) { implicit request =>
    orderRepository.getById(id) map {
      case Some(o) =>
        val body = request.body
        body.validate[UpdateOrder].fold(
          errors => {
            BadRequest(Json.obj("message" -> JsError.toJson(errors)))
          },
          order => {
            var invalidMovies: Seq[String] = Seq()
            if (order.movies.nonEmpty) {
              for (m <- order.movies.get) {
                if (!(Await.result(movieRepository.isExist(m), Duration.Inf))) {
                  invalidMovies = invalidMovies :+ m
                }
              }
            }

            val isUserExist: Boolean = Await.result(userRepository.isExist(order.user.getOrElse(o.userId)), Duration.Inf)
            val isPayExist: Boolean = Await.result(payRepository.isExist(order.pay.getOrElse(o.payId)), Duration.Inf)

            val valid: Boolean = invalidMovies.isEmpty && isUserExist && isPayExist

            if (!valid) {
              if (!isUserExist) {
                BadRequest(Json.obj("message" -> "User does not exist"))
              } else if (!isPayExist) {
                BadRequest(Json.obj("message" -> "Pay does not exist"))
              } else {
                BadRequest(Json.obj("invalid_movies" -> invalidMovies))
              }
            } else {
              val currentMovies: Seq[String] = Await.result(orderRepository.getMoviesForOrder(id), Duration.Inf).map(_.movieId)

              orderRepository.update(id, order.user.getOrElse(o.userId), order.pay.getOrElse(o.payId), order.movies.getOrElse(currentMovies))
              Ok(Json.obj("message" -> "Order updated"))
            }
          }
        )
      case None => orderNotFound
    }
  }

  def delete(id: String): Action[AnyContent] = silhouette.SecuredAction(RoleJWTAuthorization(UserRoles.Admin)).async { implicit request =>
    orderRepository.getById(id) map {
      case Some(o) =>
        orderRepository.delete(id)
        Ok(Json.obj("message" -> "Order deleted"))
      case None => orderNotFound
    }
  }
}