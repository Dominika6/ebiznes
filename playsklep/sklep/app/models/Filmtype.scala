package models

import play.api.libs.json.{Json, OFormat}

case class Filmtype(typeId: String, movieType: String)

object Filmtype{
  implicit val typeFormat: OFormat[Filmtype] = Json.format[Filmtype]
}