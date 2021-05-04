package models

import play.api.libs.json.{Json, OFormat}

case class Movie(movieId: String, title: String, publicationDate: String, price: Double, details: String)

object Movie{
  implicit val movieFormat: OFormat[Movie] = Json.format[Movie]
}
