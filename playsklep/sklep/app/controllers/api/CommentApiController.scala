package controllers.api

import javax.inject.{Inject, Singleton}
import models.{User}
import models.{Comment, Movie}
import play.api.libs.json.{JsError, Json}
import play.api.mvc._
import models.{CommentRepository, MovieRepository}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class CreateComment(comment: String,
                       movie: String)

object CreateComment {
  implicit val commentFormat = Json.format[CreateComment]
}


case class UpdateComment(comment: Option[String],
                         movie: Option[String])

object UpdateComment {
  implicit val commentFormat = Json.format[UpdateComment]
}


@Singleton
class CommentApiController @Inject()(commentRepository: CommentRepository,
                                     userRepository: UserService,
                                     movieRepository: MovieRepository,
                                     errorHandler: JsonErrorHandler,
                                     cc: MessagesControllerComponents
                                    )(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val commentNotFound = NotFound(Json.obj("message" -> "Comment does not exist"))

  def getAll: Action[AnyContent] = Action.async { implicit request =>
    val comments: Future[Seq[(Comment, Movie, User)]] = commentRepository.getAllWithMovieAndUser()
    comments.map(comment => {
      val newComment = comment.map {
        case (c, m, u) => Json.obj("id" -> c.id, "comment" -> c.comment, "movie" -> m, "user" -> u)
      }
      Ok(Json.toJson(newComment))
    })
  }

  def get(id: String) = Action.async { implicit request =>
    commentRepository.getByIdWithMovieAndUser(id) map {
      case Some(c) => Ok(Json.toJson(Json.obj("id" -> c._1.id, "comment" -> c._1.comment, "movie" -> c._2, "user" -> c._3)))
      case None => commentNotFound
    }
  }

  def create() : Action[AnyContent] = Action.async { implicit request =>
    val body = request.body.asJson.get
    body.validate[CreateComment].fold(
      errors => {
        BadRequest(Json.obj("message" -> JsError.toJson(errors)))
      },
      comment => {
        val userExist: Boolean = Await.result(userRepository.isExist(request.identity.id), Duration.Inf)
        if (!userExist) {
          BadRequest(Json.obj("message" -> "User does not exist"))
        } else {
          val movieExist: Boolean = Await.result(movieRepository.isExist(comment.movie), Duration.Inf)
          if (!movieExist) {
            BadRequest(Json.obj("message" -> "Movie does not exist"))
          } else {
            commentRepository.create(comment.comment, request.identity.id, comment.movie)
            Ok(Json.obj("message" -> "Comment created"))
          }
        }
      }
    )
  }

  def update(id: String) : Action[AnyContent] = Action.async { implicit request =>
    commentRepository.getById(id) map {
      case Some(c) => {
        val body = request.body
        body.validate[UpdateComment].fold(
          errors => {
            BadRequest(Json.obj("message" -> JsError.toJson(errors)))
          },
          comment => {
            val userExist: Boolean = Await.result(userRepository.isExist(request.identity.id), Duration.Inf)
            if (!userExist) {
              BadRequest(Json.obj("message" -> "User does not exist"))
            } else {
              val movieExist: Boolean = Await.result(movieRepository.isExist(comment.movie.getOrElse(c.movie)), Duration.Inf)
              if (!movieExist) {
                BadRequest(Json.obj("message" -> "Movie does not exist"))
              } else {
                commentRepository.update(id, comment.comment.getOrElse(c.comment), request.identity.id, comment.movie.getOrElse(c.movie))
                Ok(Json.obj("message" -> "Comment updated"))
              }
            }
          }
        )
      }
      case None => commentNotFound
    }
  }

  def delete(id: String) : Action[AnyContent] = Action.async { implicit request =>
    commentRepository.getById(id) map {
      case Some(c) => {
        commentRepository.delete(id)
        Ok(Json.obj("message" -> "Comment deleted"))
      }
      case None => commentNotFound
    }
  }
}