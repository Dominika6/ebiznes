package models

import play.api.libs.json.{Json, OFormat}

case class Director(directorId: String, firstName: String, surname: String)

object Director{
  implicit val directorFormat: OFormat[Director] = Json.format[Director]
}
