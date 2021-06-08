package controllers
import com.mohiva.play.silhouette.api.Silhouette
import javax.inject.{Inject, Singleton}
import models.auth.{User, UserRoles}
//import models.{User, UserRoles}
import models.{Comment, Movie}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import repoauth.UserService
import models.{CommentRepository, MovieRepository}
import utils.auth.{CookieEnv, RoleCookieAuthorization}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

@Singleton
class CommentController @Inject()(commentRepository: CommentRepository,
                                  userRepository: UserService,
                                  movieRepository: MovieRepository,
                                  silhouette: Silhouette[CookieEnv],
                                  cc: MessagesControllerComponents
                                 )(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {
  val createCommentForm: Form[CreateCommentForm] = Form {
    mapping(
      "comment" -> nonEmptyText,
      "userId" -> nonEmptyText,
      "movieId" -> nonEmptyText
    )(CreateCommentForm.apply)(CreateCommentForm.unapply)
  }

  def getAll: Action[AnyContent] = silhouette.SecuredAction(RoleCookieAuthorization(UserRoles.User)).async { implicit request =>
    val comments = commentRepository.getAllWithMovieAndUser
    comments.map(comment => Ok(views.html.comments(comment)))
  }

  def create: Action[AnyContent] = silhouette.SecuredAction(RoleCookieAuthorization(UserRoles.Admin)) { implicit request: Request[_]  =>
    val users: Seq[User] = Await.result(userRepository.getAll(), Duration.Inf)
    val movies: Seq[Movie] = Await.result(movieRepository.getAll, Duration.Inf)
    Ok(views.html.add_comment(createCommentForm, users, movies))
  }

  def createCommentHandler: Action[AnyContent] = silhouette.SecuredAction(RoleCookieAuthorization(UserRoles.Admin)).async { implicit request: Request[_]  =>
    val errorFunction = { formWithErrors: Form[CreateCommentForm] =>
      Future {
        Redirect(routes.CommentController.create).flashing("error" -> "Błąd podczas dodawania komentarza!")
      }
    }

    val successFunction = { comment: CreateCommentForm =>
      commentRepository.create(comment.comment, comment.userId, comment.movieId).map { _ =>
        Redirect(routes.CommentController.getAll).flashing("success" -> "Komentarz dodany!")
      };
    }
    createCommentForm.bindFromRequest.fold(errorFunction, successFunction)
  }

  def delete(commentId: String): Action[AnyContent] = silhouette.SecuredAction(RoleCookieAuthorization(UserRoles.Admin)).async { implicit request: Request[_]  =>
    commentRepository.delete(commentId).map(_ => Redirect(routes.CommentController.getAll).flashing("info" -> "Komentarz został usunięty!"))
  }

  def update(commentId: String): Action[AnyContent] = silhouette.SecuredAction(RoleCookieAuthorization(UserRoles.Admin)) { implicit request: Request[_]  =>
    val users: Seq[User] = Await.result(userRepository.getAll(), Duration.Inf)
    val movies: Seq[Movie] = Await.result(movieRepository.getAll, Duration.Inf);
    val comment: Comment = Await.result(commentRepository.getById(commentId), Duration.Inf).get
    val updateForm = createCommentForm.fill(CreateCommentForm(comment.comment, comment.userId, comment.movieId))
    Ok(views.html.update_comment(commentId, updateForm, users, movies))
  }

  def updateCommentHandler(commentId: String): Action[AnyContent] = silhouette.SecuredAction(RoleCookieAuthorization(UserRoles.Admin)).async { implicit request: Request[_]  =>
    val errorFunction = { formWithErrors: Form[CreateCommentForm] =>
      Future {
        Redirect(routes.CommentController.update(commentId)).flashing("error" -> "Błąd podczas edycji komentarza!")
      }
    }

    val successFunction = { comment: CreateCommentForm =>
      commentRepository.update(commentId, comment.comment, comment.userId, comment.movieId).map { _ =>
        Redirect(routes.CommentController.getAll).flashing("success" -> "Komentarz zmodyfikowany!")
      };
    }
    createCommentForm.bindFromRequest.fold(errorFunction, successFunction)
  }

}

case class CreateCommentForm(comment: String,
                             userId: String,
                             movieId: String)