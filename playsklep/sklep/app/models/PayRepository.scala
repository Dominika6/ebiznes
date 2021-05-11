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

  val _pay = TableQuery[PayTable]

  def getAll(): Future[Seq[Pay]] = db.run {
    _pay.result
  }

  def getById(payId: String): Future[Option[Pay]] = db.run {
    _pay.filter(_.id === payId).result.headOption
  }

  def isExist(payId: String): Future[Boolean] = db.run {
    _pay.filter(_.id === payId).exists.result
  }

  def create(name: String): Future[Int] = db.run {
    val id: String = UUID.randomUUID().toString()
    _pay.insertOrUpdate(Pay(id, name))
  }

  def delete(payId: String): Future[Int] = db.run {
    _pay.filter(_.id === payId).delete
  }

  def update(id: String, name: String): Future[Int] = db.run {
    _pay.filter(_.id === id).update(Pay(id, name))
  }

}