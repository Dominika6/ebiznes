package models
import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PayRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  val pay = TableQuery[PayTable]

  def getAll: Future[Seq[Pay]] = db.run {
    pay.result
  }

  def getById(payId: String): Future[Option[Pay]] = db.run {
    pay.filter(_.payId === payId).result.headOption
  }

  def isExist(payId: String): Future[Boolean] = db.run {
    pay.filter(_.payId === payId).exists.result
  }

  def create(name: String): Future[Int] = db.run {
    val id: String = UUID.randomUUID().toString
    pay.insertOrUpdate(Pay(id, name))
  }

  def delete(payId: String): Future[Int] = db.run {
    pay.filter(_.payId === payId).delete
  }

  def update(id: String, name: String): Future[Int] = db.run {
    pay.filter(_.payId === id).update(Pay(id, name))
  }

}