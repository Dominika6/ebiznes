package models

import play.api.libs.json.{Json, OFormat}

case class MovieAndType(movieId: String, typeId: String)

object MovieAndType{
  implicit val movieAndTypeFormat: OFormat[MovieAndType] = Json.format[MovieAndType]
}
