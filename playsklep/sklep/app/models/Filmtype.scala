package models
import play.api.libs.json.{Json, OFormat}
import slick.jdbc.SQLiteProfile.api._

case class Filmtype(filmtypeId: String, filmtype: String)

class FilmtypeTable(tag: Tag) extends Table[Filmtype](tag, "filmtype") {
  def filmtypeId = column[String]("filmtypeId", O.PrimaryKey)
  def filmtype = column[String]("filmtype")
  def * = (filmtypeId, filmtype) <> ((Filmtype.apply _).tupled, Filmtype.unapply)
}


object Filmtype{
  implicit val filmtypeFormat: OFormat[Filmtype] = Json.format[Filmtype]
}