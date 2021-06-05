package models
import play.api.libs.json.{Json, OFormat}
import slick.jdbc.SQLiteProfile.api._

case class MovieAndDirector(movieId: String, directorId: String)

class MovieAndDirectorTable(tag: Tag) extends Table[MovieAndDirector](tag, "movieAndDirector") {

  val movie = TableQuery[MovieTable]
  val director = TableQuery[DirectorTable]

  def movieId = column[String]("movieId")
  def movieId_fk = foreignKey("movieId_fk", movieId, movie)(_.movieId, onUpdate = ForeignKeyAction.NoAction , onDelete = ForeignKeyAction.Cascade)
  def directorId = column[String]("directorId")
  def directorId_fk = foreignKey("directorId_fk", directorId, director)(_.directorId, onUpdate = ForeignKeyAction.NoAction , onDelete = ForeignKeyAction.Cascade)
  def pk = primaryKey("primaryKey", (movieId, directorId))
  def * = (movieId, directorId) <> ((MovieAndDirector.apply _).tupled, MovieAndDirector.unapply)
}

object MovieAndDirector{
  implicit val movieAndDirectorFormat: OFormat[MovieAndDirector] = Json.format[MovieAndDirector]
}
