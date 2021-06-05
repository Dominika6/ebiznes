package models
import models.auth.UserTable
import play.api.libs.json.{Json, OFormat}
import slick.jdbc.SQLiteProfile.api._

case class Order (orderId: String, userId: String, payId: String)

class OrderTable(tag: Tag) extends Table[Order](tag, "order") {

  val order = TableQuery[OrderTable]
  val pay = TableQuery[PayTable]
  val user = TableQuery[UserTable]

  def orderId = column[String]("orderId", O.PrimaryKey)
  def userId = column[String]("userId")
  def userId_fk = foreignKey("userId_fk", userId, user)(_.userId, onUpdate = ForeignKeyAction.NoAction , onDelete = ForeignKeyAction.Cascade)
  def payId = column[String]("payId")
  def payId_fk = foreignKey("payId_fk", payId, pay)(_.payId, onUpdate = ForeignKeyAction.NoAction , onDelete = ForeignKeyAction.Cascade)
  def * = (orderId, userId, payId) <> ((Order.apply _).tupled, Order.unapply)
}

object Order{
  implicit val orderFormat: OFormat[Order] = Json.format[Order]
}