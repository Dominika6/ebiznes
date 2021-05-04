package models

import javax.inject.{Inject, Singleton}
import models._
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class MovieRepository @Inject()(dbConfigProvider: DatabaseConfigProvider,movieAndDirector: MovieAndDirector, movieAndTypeRepository: MovieAndTypeRepository, movieAndActorRepository: MovieAndActorRepository)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class MovieTable(tag: Tag) extends Table[Movie](tag, "movieId") {
    def movieId = column[String]("movieId", O.PrimaryKey)
    def title = column[String]("title")
    def publicationDate = column[String]("publicationDate")
    def price = column[Double]("price")
    def details = column[String]("details")
    def * = (movieId, title, publicationDate, price, details) <> ((Movie.apply _).tupled, Movie.unapply)
  }


  val movie = TableQuery[MovieTable]
  val movieAndType = TableQuery[movieAndTypeRepository.MovieAndTypeTable]
  val movieAndActor = TableQuery[movieAndActorRepository.MovieAndActorTable]

  def getAll: Future[Seq[Movie]] = db.run {
    movie.result
  }

  def getById(movieId: String): Future[Option[Movie]] = db.run {
    movie.filter(_.movieId === movieId).result.headOption
  }

  def isExist(movieId: String): Future[Boolean] = db.run {
    movie.filter(_.movieId === movieId).exists.result
  }

  def create(title: String, publicationDate: String, price: Double, details: String):Future[Movie] = db.run {
    (movie.map(m => (m.title, m.publicationDate, m.price, m.details))
      returning movie.map(_.movieId)
      into {case ((title, publicationDate, price, details),movieId) => Movie(movieId, title, publicationDate, price, details)}
      ) += (title, publicationDate, price, details)

  }

  def delete(movieId: String): Future[Int] = db.run {
    movie.filter(_.movieId === movieId).delete
  }

  def update(movieId: String, title: String, productionYear: String, price: Double , details: String): Future[Int] = db.run {
    movie.filter(_.movieId === movieId).update(Movie(movieId, title, productionYear, price, details))
  }

}