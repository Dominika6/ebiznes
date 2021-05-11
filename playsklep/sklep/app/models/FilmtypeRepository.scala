package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

@Singleton
class FilmtypeRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class TypeTable(tag: Tag) extends Table[Type](tag, "filmtype") {
    def filmtypeId = column[String]("filmtypeId", O.PrimaryKey)
    def movieType = column[String]("filmtype")
    def * = (filmtypeId, movieType) <> ((Type.apply _).tupled, Type.unapply)
  }



  val _filmtype = TableQuery[TypeTable]
  val _movieTypes = TableQuery[MovieTypeTable]

  def getAll(): Future[Seq[Type]] = db.run {
    _filmtype.result
  }

  def getById(filmtypeId: String): Future[Option[Type]] = db.run {
    _filmtype.filter(_.id === filmtypeId).result.headOption
  }

  def isExist(filmtypeId: String): Future[Boolean] = db.run {
    _filmtype.filter(_.id === filmtypeId).exists.result
  }

  def getForMovie(movieId: String): Future[Seq[Type]] = db.run {
    _movieTypes.filter(_.movie === movieId).join(_filmtype).on(_.filmtype === _.id).map {
      case (ma, g) => g
    }.result
  }

  def create(name: String): Future[Int] = db.run {
    val id: String = UUID.randomUUID().toString()
    _filmtype.insertOrUpdate(Type(id, name))
  }

  def delete(filmtypeId: String): Future[Int] = db.run {
    _filmtype.filter(_.id === filmtypeId).delete
  }

  def update(filmtypeId: String, name: String): Future[Int] = db.run {
    _filmtype.filter(_.id === filmtypeId).update(Type(filmtypeId, name))
  }



}