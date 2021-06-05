package controllers
import com.mohiva.play.silhouette.api.Silhouette
import models.auth.{User, UserRoles}
import javax.inject.{Inject, Singleton}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import repoauth.UserService
import utils.auth.{CookieEnv, RoleCookieAuthorization}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

@Singleton
class UserController @Inject()(userRepository: UserService, cc: MessagesControllerComponents,
                               silhouette: Silhouette[CookieEnv])(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {
  val createUserForm: Form[CreateUserForm] = Form {
    mapping(
      "firstName" -> nonEmptyText,
      "surname" -> nonEmptyText,
      "isAdmin" -> boolean
    )(CreateUserForm.apply)(CreateUserForm.unapply)
  }

  def getAll: Action[AnyContent] = silhouette.SecuredAction(RoleCookieAuthorization(UserRoles.Admin)).async { implicit request: Request[_] =>
    val users = userRepository.getAll();
    users.map(user => Ok(views.html.users(user)))
  }

  def delete(userId: String): Action[AnyContent] = silhouette.SecuredAction(RoleCookieAuthorization(UserRoles.Admin)).async { implicit request: Request[_] =>
    userRepository.delete(userId).map(_ => Redirect(routes.UserController.getAll()).flashing("info" -> "Użytkownik usunięty"))
  }

  def update(userId: String): Action[AnyContent] = silhouette.SecuredAction(RoleCookieAuthorization(UserRoles.Admin)) { implicit request: Request[_] =>
    val user: User = Await.result(userRepository.getById(userId), Duration.Inf).get
    val isAdmin: Boolean = user.role == UserRoles.Admin
    val updateForm = createUserForm.fill(CreateUserForm(user.firstName, user.surname, isAdmin))
    Ok(views.html.update_user(userId, updateForm, isAdmin))
  }

  def updateUserHandler(userId: String): Action[AnyContent] = silhouette.SecuredAction(RoleCookieAuthorization(UserRoles.Admin)).async { implicit request: Request[_] =>
    val errorFunction = { formWithErrors: Form[CreateUserForm] =>
      Future.successful(Redirect(routes.UserController.update(userId)).flashing("error" -> "Błąd podczas edycji użytkownika"))
    }

    val successFunction = { user: CreateUserForm =>
      val userEmail: String = Await.result(userRepository.getById(userId), Duration.Inf).get.email
      var userRole: UserRoles.UserRole = UserRoles.User
      if (user.isAdmin) {
        userRole = UserRoles.Admin
      }
      userRepository.update(userId, user.firstName, user.surname, userEmail, userRole).map { _ =>
        Redirect(routes.UserController.getAll()).flashing("success" -> "Użytkownik zmodyfikowany")
      };
    }
    createUserForm.bindFromRequest.fold(errorFunction, successFunction)
  }

}

case class CreateUserForm(firstName: String,
                          surname: String,
                          isAdmin: Boolean)