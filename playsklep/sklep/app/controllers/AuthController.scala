package controllers
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AuthenticatorService
import com.mohiva.play.silhouette.api.util.{Credentials, PasswordHasher}
import com.mohiva.play.silhouette.api.{ LoginInfo, LogoutEvent, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import javax.inject.{Inject, Singleton}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{ Action, AnyContent, MessagesAbstractController, MessagesControllerComponents}
import repoauth.UserService
import utils.auth.{CookieEnv, DashboardErrorHandler}
import scala.concurrent.{ExecutionContext, Future}

case class Registration(firstName: String,
                        surname: String,
                        email: String,
                        password: String
                      )


case class Login(email: String,
                  password: String
                 )


@Singleton
class AuthController @Inject()(cc: MessagesControllerComponents,
                                         userService: UserService,
                                         errorHandler: DashboardErrorHandler,
                                         passwordHasher: PasswordHasher,
                                         authInfoRepository: AuthInfoRepository,
                                         silhouetteCookie: Silhouette[CookieEnv],
                                         credentialsProvider: CredentialsProvider)
                                        (implicit ec: ExecutionContext)
  extends MessagesAbstractController(cc) {

  val cookieAuthService: AuthenticatorService[CookieAuthenticator] = silhouetteCookie.env.authenticatorService
  val cookieEventBus = silhouetteCookie.env.eventBus

  val registrationForm: Form[Registration] = Form {
    mapping(
      "firstName" -> nonEmptyText,
      "surname" -> nonEmptyText,
      "email" -> email,
      "password" -> nonEmptyText
    )(Registration.apply)(Registration.unapply)
  }

  val loginForm: Form[Login] = Form {
    mapping(
      "email" -> email,
      "password" -> nonEmptyText
    )(Login.apply)(Login.unapply)
  }


  def registration: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.registration(registrationForm))
  }

  def login: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.login(loginForm))
  }

  def registrationHandler: Action[AnyContent] = Action.async { implicit request =>
    val errorFunction = { formWithErrors: Form[Registration] =>
      Future {
        Redirect(routes.AuthController.registration).flashing("error" -> "Błąd podczas rejestracji!")
      }
    }

    val successFunction = { user: Registration => {
      val loginInfo = LoginInfo(CredentialsProvider.ID, user.email)
      userService.retrieve(loginInfo).flatMap {
        case Some(u) => Future {
          Redirect(routes.AuthController.registration).flashing("error" -> "Taki uzytkownik (email) juz istnieje!")
        }
        case None =>
          for {
            _ <- userService.saveOrUpdate(user.firstName, user.surname, user.email, loginInfo)
            authInfo = passwordHasher.hash(user.password)
            _ <- authInfoRepository.add(loginInfo, authInfo)
            authenticator <- cookieAuthService.create(loginInfo)
            cookie <- cookieAuthService.init(authenticator)
            result <- cookieAuthService.embed(cookie, Redirect(routes.HomeController.index()).flashing("success" -> "Witamy!"))
          } yield {
            result
          }
      }
    }
    }
    registrationForm.bindFromRequest.fold(errorFunction, successFunction)
  }

  def loginHandler: Action[AnyContent] = Action.async { implicit request =>
    val errorFunction = { formWithErrors: Form[Login] =>
      Future {
        Redirect(routes.AuthController.login).flashing("error" -> "Błąd podczas logowania!")
      }
    }

    val successFunction = { user: Login =>
      credentialsProvider.authenticate(credentials = Credentials(user.email, user.password))
        .flatMap { loginInfo =>
          cookieAuthService.create(loginInfo)
            .flatMap(cookieAuthService.init(_))
            .flatMap(cookieAuthService.embed(_, Redirect(routes.HomeController.index()).flashing("success" -> "Zalogowano poprawnie")))
        }.recover {
        case e: Exception =>
          Redirect(routes.AuthController.login).flashing("error" -> e.getMessage)
      }
    }
    loginForm.bindFromRequest.fold(errorFunction, successFunction)
  }

  def logout: Action[AnyContent] = silhouetteCookie.SecuredAction(errorHandler).async { implicit request =>
    cookieEventBus.publish(LogoutEvent(request.identity, request))
    cookieAuthService.discard(request.authenticator, Redirect(routes.AuthController.login).flashing("info" -> "Wylogowano!"))
  }
}

