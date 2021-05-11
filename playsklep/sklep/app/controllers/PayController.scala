package controllers

import javax.inject.{Inject, Singleton}
import models.Pay
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import models.PayRepository

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

@Singleton
class PayController @Inject()(payRepository: PayRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val createPayForm: Form[CreatePayForm] = Form {
    mapping(
      "method" -> nonEmptyText
    )(CreatePayForm.apply)(CreatePayForm.unapply)
  }

  def getAll : Action[AnyContent] = Action.async { implicit request: Request[_]  =>
    val pays = payRepository.getAll();
    pays.map(pay => Ok(views.html.pay.pays(pay)))
  }

  def create : Action[AnyContent] = Action.async { implicit request: Request[_]  =>
    Ok(views.html.pay.add_pay(createPayForm))
  }

  def createPayHandler: Action[AnyContent] = Action.async { implicit request: Request[_]  =>
    val errorFunction = { formWithErrors: Form[CreatePayForm] =>
      Future {
        Redirect(routes.PayController.create()).flashing("error" -> "Błąd podczas dodawania typu płatności")
      }
    }

    val successFunction = { pay: CreatePayForm =>
      payRepository.create(pay.method).map { _ =>
        Redirect(routes.PayController.getAll()).flashing("success" -> "Typ płatności dodany")
      };
    }
    createPayForm.bindFromRequest.fold(errorFunction, successFunction)
  }

  def delete(payId: String) : Action[AnyContent] = Action.async { implicit request: Request[_]  =>
    payRepository.delete(payId).map(_ => Redirect(routes.PayController.getAll()).flashing("info" -> "Typ płatności usunięty"))
  }

  def update(payId: String) : Action[AnyContent] = Action.async { implicit request: Request[_]  =>
    val pay: Pay = Await.result(payRepository.getById(payId), Duration.Inf).get
    val updateForm = createPayForm.fill(CreatePayForm(pay.method))
    Ok(views.html.pay.update_pay(payId, updateForm))
  }

  def updatePayHandler(payId: String): Action[AnyContent] = Action.async { implicit request: Request[_]  =>
    val errorFunction = { formWithErrors: Form[CreatePayForm] =>
      Future {
        Redirect(routes.PayController.update(payId)).flashing("error" -> "Błąd podczas edycji typu płatności")
      }
    }

    val successFunction = { pay: CreatePayForm =>
      payRepository.update(payId, pay.method).map { _ =>
        Redirect(routes.PayController.getAll()).flashing("success" -> "Typ płatności zmodyfikowany")
      };
    }
    createPayForm.bindFromRequest.fold(errorFunction, successFunction)
  }

}
case class CreatePayForm(method: String)