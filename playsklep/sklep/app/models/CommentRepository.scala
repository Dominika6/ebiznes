package models

import java.util.UUID
import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ExecutionContext, Future}


@Singleton
class CommentRepository @Inject()(dbConfigProvider: DatabaseConfigProvider, userRepository: UserRepository, movieRepository: MovieRepository)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class CommentTable(tag: Tag) extends Table[Comment](tag, "comment") {
    val user = TableQuery[userRepository.UserTable]
    val movie = TableQuery[movieRepository.MovieTable]

    def commentId = column[String]("commentId", O.PrimaryKey)
    def comment = column[String]("comment")
    def userId = column[String]("userId")
    def movieId = column[String]("movieId")
    def userId_fk = foreignKey("user_fk", userId, user)(_.userId, onUpdate = ForeignKeyAction.NoAction , onDelete = ForeignKeyAction.Cascade)
    def movieId_fk = foreignKey("movieId_fk", movieId, movie)(_.movieId, onUpdate = ForeignKeyAction.NoAction , onDelete = ForeignKeyAction.Cascade)
    def * = (commentId, comment, userId, movieId) <> ((Comment.apply _).tupled, Comment.unapply)
  }

  val tableComment = TableQuery[CommentTable]
  val user = TableQuery[userRepository.UserTable]
  val movie = TableQuery[movieRepository.MovieTable]

  def create(comment: String, userId: String, movieId: String): Future[Nothing] = db.run {
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

  def update(commentId: String, comment: String, userId: String, movieId: String): Future[Nothing] = db.run {
    tableComment.filter(_.commentId === commentId).update(Comment(commentId, comment, userId, movieId))
  }

}