package models
import play.api.libs.json.{Json, OFormat}
import slick.jdbc.SQLiteProfile.api._

case class Director(directorId: String, firstName: String, surname: String)

class DirectorTable(tag: Tag) extends Table[Director](tag, "director") {
  def directorId = column[String]("directorId", O.PrimaryKey)
  def firstName = column[String]("firstName")
  def surname = column[String]("surname")
  def * = (directorId, firstName, surname) <> ((Director.apply _).tupled, Director.unapply)
}

object Director{
  implicit val directorFormat: OFormat[Director] = Json.format[Director]
}
