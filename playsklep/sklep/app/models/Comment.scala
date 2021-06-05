package models
import models.auth.UserTable
import slick.jdbc.SQLiteProfile.api._
import play.api.libs.json.{Json, OFormat}

case class Comment(commentId: String, comment: String, userId: String, movieId: String)

class CommentTable(tag: Tag) extends Table[Comment](tag, "comment") {
  val user = TableQuery[UserTable]
  val movie = TableQuery[MovieTable]

  def commentId = column[String]("commentId", O.PrimaryKey)
  def comment = column[String]("comment")
  def userId = column[String]("userId")
  def movieId = column[String]("movieId")
  def userId_fk = foreignKey("userId_fk", userId, user)(_.userId, onUpdate = ForeignKeyAction.NoAction , onDelete = ForeignKeyAction.Cascade)
  def movieId_fk = foreignKey("movieId_fk", movieId, movie)(_.movieId, onUpdate = ForeignKeyAction.NoAction , onDelete = ForeignKeyAction.Cascade)
  def * = (commentId, comment, userId, movieId) <> ((Comment.apply _).tupled, Comment.unapply)
}

object Comment{
  implicit val commentFormat: OFormat[Comment] = Json.format[Comment]
}