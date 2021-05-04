package models

import play.api.libs.json.{Json, OFormat}

case class MovieAndActor(movieId: String, actorId: String)

object MovieAndActor{
  implicit val movieAndActorFormat: OFormat[MovieAndActor] = Json.format[MovieAndActor]
}