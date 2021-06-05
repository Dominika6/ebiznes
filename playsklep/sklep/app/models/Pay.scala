package models
import play.api.libs.json.{Json, OFormat}
import slick.jdbc.SQLiteProfile.api._

case class Pay(payId: String, method: String)

class PayTable(tag: Tag) extends Table[Pay](tag, "pay") {
  def payId = column[String]("payId", O.PrimaryKey)
  def method = column[String]("method")
  def * = (payId, method) <> ((Pay.apply _).tupled, Pay.unapply)
}

object Pay{
  implicit val payFormat: OFormat[Pay] = Json.format[Pay]
}
