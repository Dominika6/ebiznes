package controllers
import com.mohiva.play.silhouette.api.Silhouette
import javax.inject.{Inject, Singleton}
import models.Pay
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import models.PayRepository
import models.auth.UserRoles
import utils.auth.{CookieEnv, RoleCookieAuthorization}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

@Singleton
class PayController @Inject()(payRepository: PayRepository, cc: MessagesControllerComponents,
                              silhouette: Silhouette[CookieEnv])(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val createPayForm: Form[CreatePayForm] = Form {
    mapping(
      "method" -> nonEmptyText
    )(CreatePayForm.apply)(CreatePayForm.unapply)
  }

  def getAll: Action[AnyContent] = silhouette.SecuredAction(RoleCookieAuthorization(UserRoles.User)).async { implicit request: Request[_]  =>
    val pays = payRepository.getAll
    pays.map(pay => Ok(views.html.pays(pay)))
  }

  def create: Action[AnyContent] = silhouette.SecuredAction(RoleCookieAuthorization(UserRoles.Admin)) { implicit request: Request[_]  =>
    Ok(views.html.add_pay(createPayForm))
  }

  def createPayHandler: Action[AnyContent] = silhouette.SecuredAction(RoleCookieAuthorization(UserRoles.Admin)).async { implicit request: Request[_]  =>
    val errorFunction = { formWithErrors: Form[CreatePayForm] =>
      Future {
        Redirect(routes.PayController.create).flashing("error" -> "Błąd podczas dodawania typu płatności")
      }
    }

    val successFunction = { pay: CreatePayForm =>
      payRepository.create(pay.method).map { _ =>
        Redirect(routes.PayController.getAll).flashing("success" -> "Typ płatności dodany")
      };
    }
    createPayForm.bindFromRequest.fold(errorFunction, successFunction)
  }

  def delete(payId: String): Action[AnyContent] = silhouette.SecuredAction(RoleCookieAuthorization(UserRoles.Admin)).async { implicit request: Request[_]  =>
    payRepository.delete(payId).map(_ => Redirect(routes.PayController.getAll).flashing("info" -> "Typ płatności usunięty"))
  }

  def update(payId: String): Action[AnyContent] = silhouette.SecuredAction(RoleCookieAuthorization(UserRoles.Admin)) { implicit request: Request[_]  =>
    val pay: Pay = Await.result(payRepository.getById(payId), Duration.Inf).get
    val updateForm = createPayForm.fill(CreatePayForm(pay.method))
    Ok(views.html.update_pay(payId, updateForm))
  }

  def updatePayHandler(payId: String): Action[AnyContent] = silhouette.SecuredAction(RoleCookieAuthorization(UserRoles.Admin)).async { implicit request: Request[_]  =>
    val errorFunction = { formWithErrors: Form[CreatePayForm] =>
      Future {
        Redirect(routes.PayController.update(payId)).flashing("error" -> "Błąd podczas edycji typu płatności")
      }
    }

    val successFunction = { pay: CreatePayForm =>
      payRepository.update(payId, pay.method).map { _ =>
        Redirect(routes.PayController.getAll).flashing("success" -> "Typ płatności zmodyfikowany")
      };
    }
    createPayForm.bindFromRequest.fold(errorFunction, successFunction)
  }

}
case class CreatePayForm(method: String)