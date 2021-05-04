package models

import play.api.libs.json.{Json, OFormat}

case class Order (orderId: String, userId: String, payId: String)

object Order{
  implicit val orderFormat: OFormat[Order] = Json.format[Order]
}