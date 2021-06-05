package models
import play.api.libs.json.{Json, OFormat}
import slick.jdbc.SQLiteProfile.api._

case class MovieToOrder(movieId: String, price: Double, orderId: String)

class OrderItemTable(tag: Tag) extends Table[MovieToOrder](tag, "movieToOrder") {
  val movie = TableQuery[MovieTable]
  val order = TableQuery[OrderTable]

  def orderId = column[String]("orderId")
  def orderId_fk = foreignKey("orderId_fk", orderId, order)(_.orderId, onUpdate = ForeignKeyAction.NoAction , onDelete = ForeignKeyAction.Cascade)
  def movieId = column[String]("movieId")
  def movieId_fk = foreignKey("movieId_fk", movieId, movie)(_.movieId, onUpdate = ForeignKeyAction.NoAction , onDelete = ForeignKeyAction.Cascade)
  def price = column[Double]("price")
  def pk = primaryKey("primaryKey", (orderId, movieId))
  def * = (movieId, price, orderId) <> ((MovieToOrder.apply _).tupled, MovieToOrder.unapply)
}

object MovieToOrder {
  implicit val movieToOrderFormat: OFormat[MovieToOrder] = Json.format[MovieToOrder]
}