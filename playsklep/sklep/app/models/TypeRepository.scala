package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

@Singleton
class TypeRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class TypeTable(tag: Tag) extends Table[Type](tag, "type") {
    def typeId = column[String]("typeId", O.PrimaryKey)
    def movieType = column[String]("type")
    def * = (typeId, movieType) <> ((Type.apply _).tupled, Type.unapply)
  }

}