package controllers
import com.mohiva.play.silhouette.api.Silhouette
import javax.inject.{Inject, Singleton}
import models._
import models.auth.{User, UserRoles}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import repoauth.UserService
import utils.auth.{CookieEnv, RoleCookieAuthorization}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

@Singleton
class OrderController @Inject()(
                                 orderRepository: OrderRepository,
                                 movieRepository: MovieRepository,
                                 userRepository: UserService,
                                 payRepository: PayRepository,
                                 cc: MessagesControllerComponents,
                                 silhouette: Silhouette[CookieEnv])(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val createOrderForm: Form[CreateOrderForm] = Form {
    mapping(
      "user" -> nonEmptyText,
      "pay" -> nonEmptyText,
      "movies" -> seq(nonEmptyText)
    )(CreateOrderForm.apply)(CreateOrderForm.unapply)
  }

  def getAll: Action[AnyContent] = silhouette.SecuredAction(RoleCookieAuthorization(UserRoles.Admin)).async { implicit request: Request[_]  =>
    val orders = orderRepository.getAllWithPayAndUser
    orders.map(order => Ok(views.html.orders(order)))
  }

  def get(orderId: String): Action[AnyContent] = silhouette.SecuredAction(RoleCookieAuthorization(UserRoles.Admin)).async { implicit request: Request[_]  =>
    val orderItems: Seq[(MovieToOrder, Movie)] = Await.result(orderRepository.getOrderItemsWithMovie(orderId), Duration.Inf)
    val value: Double = Await.result(orderRepository.getOrderValue(orderId), Duration.Inf)

    orderRepository.getByIdWithUserAndPay(orderId) map {
      case Some(o) => Ok(views.html.order(o, orderItems, value))
      case None => Redirect(routes.OrderController.getAll())
    }
  }

  def create: Action[AnyContent] = silhouette.SecuredAction(RoleCookieAuthorization(UserRoles.Admin)) { implicit request: Request[_] =>
    val users: Seq[User] = Await.result(userRepository.getAll(), Duration.Inf)
    val pays: Seq[Pay] = Await.result(payRepository.getAll, Duration.Inf)
    val movies: Seq[Movie] = Await.result(movieRepository.getAll, Duration.Inf)
    Ok(views.html.add_order(createOrderForm, users, pays, movies))
  }

  def createOrderHandler: Action[AnyContent] = silhouette.SecuredAction(RoleCookieAuthorization(UserRoles.Admin)).async { implicit request: Request[_]  =>
    val errorFunction = { formWithErrors: Form[CreateOrderForm] =>
      Future {
        Redirect(routes.OrderController.create()).flashing("error" -> "Błąd podczas składania zamówienia")
      }
    }

    val successFunction = { order: CreateOrderForm =>
      orderRepository.create(order.user, order.pay, order.movies).map { _ =>
        Redirect(routes.OrderController.getAll()).flashing("success" -> "Zamówienie złożone")
      };
    }
    createOrderForm.bindFromRequest.fold(errorFunction, successFunction)
  }


  def update(orderId: String): Action[AnyContent] = silhouette.SecuredAction(RoleCookieAuthorization(UserRoles.Admin)) { implicit request: Request[_]  =>
    val order: Order = Await.result(orderRepository.getById(orderId), Duration.Inf).get

    val users: Seq[User] = Await.result(userRepository.getAll(), Duration.Inf)
    val pays: Seq[Pay] = Await.result(payRepository.getAll, Duration.Inf)

    val movies: Seq[Movie] = Await.result(movieRepository.getAll, Duration.Inf)
    val selectedMovies: Seq[String] = Await.result(orderRepository.getMoviesForOrder(orderId), Duration.Inf).map(_.movieId)

    val updateForm = createOrderForm.fill(CreateOrderForm(order.userId, order.payId, selectedMovies))

    Ok(views.html.update_order(orderId, updateForm, users, pays, selectedMovies, movies))
  }

  def updateOrderHandler(orderId: String): Action[AnyContent] = silhouette.SecuredAction(RoleCookieAuthorization(UserRoles.Admin)).async { implicit request: Request[_]  =>
    val errorFunction = { formWithErrors: Form[CreateOrderForm] =>
      Future {
        Redirect(routes.OrderController.update(orderId)).flashing("error" -> "Błąd podczas edycji zamówienia")
      }
    }

    val successFunction = { order: CreateOrderForm =>
      orderRepository.update(orderId, order.user, order.pay, order.movies).map { _ =>
        Redirect(routes.OrderController.getAll()).flashing("success" -> "Zamówienie zmodyfikowane")
      };
    }
    createOrderForm.bindFromRequest.fold(errorFunction, successFunction)
  }

  def delete(orderId: String): Action[AnyContent] = silhouette.SecuredAction(RoleCookieAuthorization(UserRoles.Admin)).async { implicit request: Request[_]  =>
    orderRepository.delete(orderId).map(_ => Redirect(routes.OrderController.getAll()).flashing("info" -> "Zamówienie usunięte"))
  }
}

case class CreateOrderForm(user: String,
                           pay: String,
                           movies: Seq[String]
                          )