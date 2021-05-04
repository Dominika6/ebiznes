package models

import play.api.libs.json.{Json, OFormat}

case class MovieToOrder(movieId: String, price: Double, orderId: String)

object MovieToOrder {
  implicit val movieToOrderFormat: OFormat[MovieToOrder] = Json.format[MovieToOrder]
}