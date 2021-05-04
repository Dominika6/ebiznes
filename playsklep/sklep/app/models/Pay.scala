package models

import play.api.libs.json.{Json, OFormat}

case class Pay(payId: String, method: String)

object Pay{
  implicit val payFormat: OFormat[Pay] = Json.format[Pay]
}
