package models

import play.api.libs.json.{Json, OFormat}

case class Rate(rateId: String, userId: String, movieId: String,result: Int )

object Rate{
  implicit val rateFormat: OFormat[Rate] = Json.format[Rate]
}