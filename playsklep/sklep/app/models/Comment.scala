package models

import play.api.libs.json.{Json, OFormat}

case class Comment(commentId: String, comment: String, userId: String, movieId: String)

object Comment{
  implicit val commentFormat: OFormat[Comment] = Json.format[Comment]
}