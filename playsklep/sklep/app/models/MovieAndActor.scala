package models
import play.api.libs.json.{Json, OFormat}
import slick.jdbc.SQLiteProfile.api._

case class MovieAndActor(movieId: String, actorId: String)

class MovieAndActorTable(tag: Tag) extends Table[MovieAndActor](tag, "movieAndActor") {
  val movie = TableQuery[MovieTable]
  val actor = TableQuery[ActorTable]

  def movieId = column[String]("movieId")
  def movieId_fk = foreignKey("movieId_fk", movieId, movie)(_.movieId, onUpdate = ForeignKeyAction.NoAction , onDelete = ForeignKeyAction.Cascade)
  def actorId = column[String]("actorId")
  def actorId_fk = foreignKey("actorId_fk", actorId, actor)(_.actorId, onUpdate = ForeignKeyAction.NoAction , onDelete = ForeignKeyAction.Cascade)
  def pk = primaryKey("primaryKey", (movieId, actorId))
  def * = (movieId, actorId) <> ((MovieAndActor.apply _).tupled, MovieAndActor.unapply)
}

object MovieAndActor{
  implicit val movieAndActorFormat: OFormat[MovieAndActor] = Json.format[MovieAndActor]
}