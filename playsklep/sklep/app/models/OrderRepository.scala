package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

@Singleton
class OrderRepository @Inject()(payRepository: PayRepository, userRepository: UserRepository, dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class OrderTable(tag: Tag) extends Table[Order](tag, "order") {

    val order = TableQuery[OrderTable]
    val pay = TableQuery[payRepository.PayTable]
    val user = TableQuery[userRepository.UserTable]

    def orderId = column[String]("orderId", O.PrimaryKey)
    def userId = column[String]("userId")
    def userId_fk = foreignKey("userId_fk", userId, user)(_.userId, onUpdate = ForeignKeyAction.NoAction , onDelete = ForeignKeyAction.Cascade)
    def payId = column[String]("payId")
    def payId_fk = foreignKey("payId_fk", payId, pay)(_.payId, onUpdate = ForeignKeyAction.NoAction , onDelete = ForeignKeyAction.Cascade)
    def * = (orderId, userId, payId) <> ((Order.apply _).tupled, Order.unapply)
  }

}