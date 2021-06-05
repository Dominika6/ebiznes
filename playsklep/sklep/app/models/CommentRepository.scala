package models
import models.auth.{User, UserTable}
import java.util.UUID
import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CommentRepository @Inject()(dbConfigProvider: DatabaseConfigProvider, userRepo: User, movieRepository: MovieRepository)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  val tableComment = TableQuery[CommentTable]
  val user = TableQuery[UserTable]
  val movie = TableQuery[MovieTable]

  def create(comment: String, userId: String, movieId: String) = db.run {
    val commentId: String = UUID.randomUUID().toString
    tableComment.insertOrUpdate(Comment(commentId, comment, userId, movieId))
  }

  def getAll: Future[Seq[Comment]] = db.run {
    tableComment.result
  }

  def getById(commentId: String): Future[Option[Comment]] = db.run {
    tableComment.filter(_.commentId === commentId).result.headOption
  }

  def getForMovie(movieId: String): Future[Seq[(Comment, User)]] = db.run {
    tableComment.filter(_.movieId === movieId).join(user).on(_.userId === _.userId).result
  }

  def delete(commentId: String): Future[Int] = db.run {
    tableComment.filter(_.commentId === commentId).delete
  }

  def update(commentId: String, comment: String, userId: String, movieId: String) = db.run {
    tableComment.filter(_.commentId === commentId).update(Comment(commentId, comment, userId, movieId))
  }


  def getByIdWithMovieAndUser(commentId: String): Future[Option[(Comment, Movie, User)]] = db.run {
    tableComment.filter(_.commentId === commentId).join(movie).on(_.movieId === _.movieId).join(user).on(_._1.userId === _.userId).map {
      case ((comment, movie), user) => (comment, movie, user)
    }.result.headOption
  }

  def getAllWithMovieAndUser: Future[Seq[(Comment, Movie, User)]] = db.run {
    tableComment.join(movie).on(_.movieId === _.movieId).join(user).on(_._1.userId === _.userId).map {
      case ((comment, movie), user) => (comment, movie, user)
    }.result
  }

}