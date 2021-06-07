package models
import play.api.libs.json.{Json, OFormat}
import slick.jdbc.SQLiteProfile.api._

case class MovieAndFilmtype(movieId: String, filmtypeId: String)

class MovieAndFilmtypeTable(tag: Tag) extends Table[MovieAndFilmtype](tag, "movieAndFilmtypeTable") {

  val movie = TableQuery[MovieTable]
  val filmtype = TableQuery[FilmtypeTable]

  def movieId = column[String]("movieId")
  def movie_fk = foreignKey("movie_fk", movieId, movie)(_.movieId, onUpdate = ForeignKeyAction.NoAction , onDelete = ForeignKeyAction.Cascade)
  def filmtypeId = column[String]("filmtypeId")
  def filmtype_fk = foreignKey("filmtype_fk", filmtypeId, filmtype)(_.filmtypeId, onUpdate = ForeignKeyAction.NoAction , onDelete = ForeignKeyAction.Cascade)
  def pk = primaryKey("primaryKey", (movieId, filmtypeId))
  def * = (movieId, filmtypeId) <> ((MovieAndFilmtype.apply _).tupled, MovieAndFilmtype.unapply)
}

object MovieAndFilmtype{
  implicit val movieAndFilmtypeFormat: OFormat[MovieAndFilmtype] = Json.format[MovieAndFilmtype]
}
