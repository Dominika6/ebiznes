package models

import play.api.libs.json.{Json, OFormat}

case class MovieAndDirector(movieId: String, directorId: String)

object MovieAndDirector{
  implicit val movieAndDirectorFormat: OFormat[MovieAndDirector] = Json.format[MovieAndDirector]
}
