package models

import java.util.UUID
import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DirectorRepository @Inject()(dbConfigProvider: DatabaseConfigProvider, MovieAndDirectorRepository: MovieAndDirectorRepository)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class DirectorTable(tag: Tag) extends Table[Director](tag, "director") {
    def directorId = column[String]("directorId", O.PrimaryKey)
    def firstName = column[String]("firstName")
    def surname = column[String]("surname")
    def * = (directorId, firstName, surname) <> ((Director.apply _).tupled, Director.unapply)
  }

  val director = TableQuery[DirectorTable]
  val movieDirectors = TableQuery[MovieAndDirectorRepository.MovieAndDirectorTable]

  def getAll: Future[Seq[Director]] = db.run {
    director.result
  }

  def getByIdOption(directorId: String): Future[Option[Director]] = db.run {
    director.filter(_.directorId === directorId).result.headOption
  }

  def create(firstName: String, surname: String): Future[Int] = db.run {
    val directorId: String = UUID.randomUUID().toString
    director.insertOrUpdate(Director(directorId, firstName, surname))
  }

  def delete(directorId: String): Future[Int] = db.run {
    director.filter(_.directorId === directorId).delete
  }

  def update(directorId: String, firstName: String, surname: String): Future[Int] = db.run {
    director.filter(_.directorId === directorId).update(Director(directorId, firstName, surname))
  }

}