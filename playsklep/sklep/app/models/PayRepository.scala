package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

@Singleton
class PayRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class PayTable(tag: Tag) extends Table[Pay](tag, "pay") {
    def payId = column[String]("payId", O.PrimaryKey)
    def method = column[String]("method")
    def * = (payId, method) <> ((Pay.apply _).tupled, Pay.unapply)
  }

}