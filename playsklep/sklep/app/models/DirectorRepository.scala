package models
import java.util.UUID
import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DirectorRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  val director = TableQuery[DirectorTable]
  val movieDirectors = TableQuery[MovieAndDirectorTable]

  def getAll: Future[Seq[Director]] = db.run {
    director.result
  }

  def getForMovie(movieId: String): Future[Seq[Director]] = db.run {
    movieDirectors.filter(_.movieId === movieId).join(director).on(_.directorId === _.directorId).map {
      case (ma, d) => d
    }.result
  }

  def getById(directorId: String): Future[Option[Director]] = db.run {
    director.filter(_.directorId === directorId).result.headOption
  }

  def create(firstName: String, surname: String): Future[Int] = db.run {
    val directorId: String = UUID.randomUUID().toString
    director.insertOrUpdate(Director(directorId, firstName, surname))
  }

  def isExist(directorId: String): Future[Boolean] = db.run {
    director.filter(_.directorId === directorId).exists.result
  }

  def delete(directorId: String): Future[Int] = db.run {
    director.filter(_.directorId === directorId).delete
  }

  def update(directorId: String, firstName: String, surname: String): Future[Int] = db.run {
    director.filter(_.directorId === directorId).update(Director(directorId, firstName, surname))
  }

}