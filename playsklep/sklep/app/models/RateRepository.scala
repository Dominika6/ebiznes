package models
import models.auth.User
import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ExecutionContext, Future}
import models.auth.UserTable
import java.util.UUID

@Singleton
class RateRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  val rate = TableQuery[RateTable]
  val user = TableQuery[UserTable]
  val movie = TableQuery[MovieTable]

  def getAll: Future[Seq[Rate]] = db.run {
    rate.result
  }

  def getAllWithMovieAndUser: Future[Seq[(Rate, Movie, User)]] = db.run {
    rate.join(user).on(_.userId === _.userId).join(movie).on(_._1.movieId === _.movieId).map {
      case ((rate, user), movie) => (rate, movie, user)
    }.result
  }

  def getById(rateId: String): Future[Option[Rate]] = db.run {
    rate.filter(_.rateId === rateId).result.headOption
  }

  def getByIdWithMovieAndUser(rateId: String): Future[Option[(Rate, Movie, User)]] = db.run {
    rate.filter(_.rateId === rateId).join(movie).on(_.movieId === _.movieId).join(user).on(_._1.userId === _.userId).map {
      case ((rate, movie), user) => (rate, movie, user)
    }.result.headOption
  }

  def getForMovie(movieId: String): Future[Seq[(Rate, User)]] = db.run {
    rate.filter(_.movieId === movieId).join(user).on(_.userId === _.userId).result
  }

  def create(value: Int, userId: String, movieId: String) = db.run {
    val newId: String = UUID.randomUUID().toString
    rate.filter(_.userId === userId).filter(_.movieId === movieId).result.headOption.flatMap {
      case Some(r) => rate.insertOrUpdate(Rate(r.rateId, userId, movieId, value));
      case None => rate.insertOrUpdate(Rate(newId, userId, movieId, value));
    }
  }

  def delete(rateId: String): Future[Int] = db.run {
    rate.filter(_.rateId === rateId).delete
  }

  def update(rateId: String, value: Int, userId: String, movieId: String): Future[Int] = db.run {
    rate.filter(_.rateId === rateId).update(Rate(rateId, userId, movieId, value))
  }
}