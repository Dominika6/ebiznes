package models
import play.api.libs.json.{Json, OFormat}
import slick.jdbc.SQLiteProfile.api._

case class Actor(actorId: String, firstName: String, surname: String)

class ActorTable(tag: Tag) extends Table[Actor](tag, "actor") {
  def actorId = column[String]("actorId", O.PrimaryKey, O.AutoInc)
  def firstName = column[String]("firstName")
  def surname = column[String]("surname")
  def * = (actorId, firstName, surname) <> ((Actor.apply _).tupled, Actor.unapply)
}

object Actor{
  implicit val actorFormat: OFormat[Actor] = Json.format[Actor]
}