package models

import play.api.libs.json.{Json, OFormat}

case class Actor(actorId: String, firstName: String, surname: String)

object Actor{
  implicit val actorFormat: OFormat[Actor] = Json.format[Actor]
}