package models

import play.api.libs.json.{Json, OFormat}

case class Type(typeId: String, movieType: String)

object Type{
  implicit val typeFormat: OFormat[Type] = Json.format[Type]
}