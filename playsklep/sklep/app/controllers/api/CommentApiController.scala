package controllers.api
import com.mohiva.play.silhouette.api.Silhouette
import models.auth.{User, UserRoles}
import javax.inject.{Inject, Singleton}
import models.{Comment, Movie}
import play.api.libs.json.{JsError, JsValue, Json, OFormat}
import play.api.mvc._
import models.{CommentRepository, MovieRepository}
import repoauth.UserService
import utils.auth.{JsonErrorHandler, JwtEnv, RoleJWTAuthorization}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class CreateComment(comment: String,
                       movie: String)

object CreateComment {
  implicit val commentFormat: OFormat[CreateComment] = Json.format[CreateComment]
}


case class UpdateComment(comment: Option[String],
                         movie: Option[String])

object UpdateComment {
  implicit val commentFormat: OFormat[UpdateComment] = Json.format[UpdateComment]
}


@Singleton
class CommentApiController @Inject()(commentRepository: CommentRepository,
                                     userRepository: UserService,
                                     movieRepository: MovieRepository,
                                     silhouette: Silhouette[JwtEnv],
                                     errorHandler: JsonErrorHandler,
                                     cc: MessagesControllerComponents
                                    )(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val commentNotFound: Result = NotFound(Json.obj("message" -> "Comment does not exist"))

  def getAll: Action[AnyContent] = Action.async { implicit request =>
    val comments: Future[Seq[(Comment, Movie, User)]] = commentRepository.getAllWithMovieAndUser
    comments.map(comment => {
      val newComment = comment.map {
        case (c, m, u) => Json.obj("commentId" -> c.commentId, "comment" -> c.comment, "movie" -> m, "user" -> u)
      }
      Ok(Json.toJson(newComment))
    })
  }

  def get(id: String): Action[AnyContent] = Action.async { implicit request =>
    commentRepository.getByIdWithMovieAndUser(id) map {
      case Some(c) => Ok(Json.toJson(Json.obj("commentId" -> c._1.commentId, "comment" -> c._1.comment, "movie" -> c._2, "user" -> c._3)))
      case None => commentNotFound
    }
  }

  def create(): Action[AnyContent] = silhouette.SecuredAction(RoleJWTAuthorization(UserRoles.User)) { implicit request =>
    val body = request.body.asJson.get
    body.validate[CreateComment].fold(
      errors => {
        BadRequest(Json.obj("message" -> JsError.toJson(errors)))
      },
      comment => {
        val userExist: Boolean = Await.result(userRepository.isExist(request.identity.userId), Duration.Inf)
        if (!userExist) {
          BadRequest(Json.obj("message" -> "User does not exist"))
        } else {
          val movieExist: Boolean = Await.result(movieRepository.isExist(comment.movie), Duration.Inf)
          if (!movieExist) {
            BadRequest(Json.obj("message" -> "Movie does not exist"))
          } else {
            commentRepository.create(comment.comment, request.identity.userId, comment.movie)
            Ok(Json.obj("message" -> "Comment created"))
          }
        }
      }
    )
  }

  def update(id: String): Action[JsValue] = silhouette.SecuredAction(RoleJWTAuthorization(UserRoles.User)).async(parse.json) { implicit request =>
    commentRepository.getById(id) map {
      case Some(c) =>
        val body = request.body
        body.validate[UpdateComment].fold(
          errors => {
            BadRequest(Json.obj("message" -> JsError.toJson(errors)))
          },
          comment => {
            val userExist: Boolean = Await.result(userRepository.isExist(request.identity.userId), Duration.Inf)
            if (!userExist) {
              BadRequest(Json.obj("message" -> "User does not exist"))
            } else {
              val movieExist: Boolean = Await.result(movieRepository.isExist(comment.movie.getOrElse(c.movieId)), Duration.Inf)
              if (!movieExist) {
                BadRequest(Json.obj("message" -> "Movie does not exist"))
              } else {
                commentRepository.update(id, comment.comment.getOrElse(c.comment), request.identity.userId, comment.movie.getOrElse(c.movieId))
                Ok(Json.obj("message" -> "Comment updated"))
              }
            }
          }
        )
      case None => commentNotFound
    }
  }

  def delete(id: String): Action[AnyContent] = silhouette.SecuredAction(RoleJWTAuthorization(UserRoles.Admin)).async { implicit request =>
    commentRepository.getById(id) map {
      case Some(c) =>
        commentRepository.delete(id)
        Ok(Json.obj("message" -> "Comment deleted"))
      case None => commentNotFound
    }
  }
}