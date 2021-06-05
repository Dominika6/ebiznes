package models
import play.api.libs.json.{Json, OFormat}
import slick.jdbc.SQLiteProfile.api._

case class Movie(movieId: String, title: String, publicationDate: String, price: Double, details: String)

class MovieTable(tag: Tag) extends Table[Movie](tag, "movieId") {
  def movieId = column[String]("movieId", O.PrimaryKey)
  def title = column[String]("title")
  def publicationDate = column[String]("publicationDate")
  def price = column[Double]("price")
  def details = column[String]("details")
  def * = (movieId, title, publicationDate, price, details) <> ((Movie.apply _).tupled, Movie.unapply)
}

object Movie{
  implicit val movieFormat: OFormat[Movie] = Json.format[Movie]
}
