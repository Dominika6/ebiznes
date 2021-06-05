package models
import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FilmtypeRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  val filmtype = TableQuery[FilmtypeTable]
  val movieAndFilmtype = TableQuery[MovieAndFilmtypeTable]

  def getAll: Future[Seq[Filmtype]] = db.run {
    filmtype.result
  }

  def getById(filmtypeId: String): Future[Option[Filmtype]] = db.run {
    filmtype.filter(_.filmtypeId === filmtypeId).result.headOption
  }

  def isExist(filmtypeId: String): Future[Boolean] = db.run {
    filmtype.filter(_.filmtypeId === filmtypeId).exists.result
  }

  def getForMovie(movieId: String): Future[Seq[Filmtype]] = db.run {
    movieAndFilmtype.filter(_.movieId === movieId).join(filmtype).on(_.filmtypeId === _.filmtypeId).map {
      case (ma, g) => g
    }.result
  }

  def create(name: String): Future[Int] = db.run {
    val id: String = UUID.randomUUID().toString
    filmtype.insertOrUpdate(Filmtype(id, name))
  }

  def delete(filmtypeId: String): Future[Int] = db.run {
    filmtype.filter(_.filmtypeId === filmtypeId).delete
  }

  def update(filmtypeId: String, name: String): Future[Int] = db.run {
    filmtype.filter(_.filmtypeId === filmtypeId).update(Filmtype(filmtypeId, name))
  }
}