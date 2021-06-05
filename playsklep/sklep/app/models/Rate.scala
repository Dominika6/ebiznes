package models
import models.auth.UserTable
import play.api.libs.json.{Json, OFormat}
import slick.jdbc.SQLiteProfile.api._

case class Rate(rateId: String, userId: String, movieId: String, result: Int )

class RateTable(tag: Tag) extends Table[Rate](tag, "rate") {
  val user = TableQuery[UserTable]
  val movie = TableQuery[MovieTable]

  def rateId = column[String]("rateId", O.PrimaryKey)
  def userId = column[String]("userId")
  def movieId = column[String]("movieId")
  def result = column[Int]("result")
  def userId_fk = foreignKey("userId_fk", userId, user)(_.userId, onUpdate = ForeignKeyAction.NoAction , onDelete = ForeignKeyAction.Cascade)
  def movieId_fk = foreignKey("movieId_fk", movieId, movie)(_.movieId, onUpdate = ForeignKeyAction.NoAction , onDelete = ForeignKeyAction.Cascade)
  def * = (rateId, userId, movieId, result) <> ((Rate.apply _).tupled, Rate.unapply)
}

object Rate{
  implicit val rateFormat: OFormat[Rate] = Json.format[Rate]
}