package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RateRepository @Inject()(dbConfigProvider: DatabaseConfigProvider, userRepository: UserRepository, movieRepository: MovieRepository)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class RateTable(tag: Tag) extends Table[Rate](tag, "rate") {
    val user = TableQuery[userRepository.UserTable]
    val movie = TableQuery[movieRepository.MovieTable]

    def rateId = column[String]("rateId", O.PrimaryKey)
    def userId = column[String]("userId")
    def movieId = column[String]("movieId")
    def result = column[Int]("result")
    def userId_fk = foreignKey("userId_fk", userId, user)(_.userId, onUpdate = ForeignKeyAction.NoAction , onDelete = ForeignKeyAction.Cascade)
    def movieId_fk = foreignKey("movieId_fk", movieId, movie)(_.movieId, onUpdate = ForeignKeyAction.NoAction , onDelete = ForeignKeyAction.Cascade)
    def * = (rateId, userId, movieId, result) <> ((Rate.apply _).tupled, Rate.unapply)
  }

  val _rate = TableQuery[RateTable]
  val _user = TableQuery[UserTable]
  val _movie = TableQuery[MovieTable]

  def getAll(): Future[Seq[Rate]] = db.run {
    _rate.result
  }

  def getAllWithMovieAndUser(): Future[Seq[(Rate, Movie, User)]] = db.run {
    _rate.join(_user).on(_.user === _.id).join(_movie).on(_._1.movie === _.id).map {
      case ((rate, user), movie) => (rate, movie, user)
    }.result
  }

  def getById(rateId: String): Future[Option[Rate]] = db.run {
    _rate.filter(_.id === rateId).result.headOption
  }

  def getByIdWithMovieAndUser(rateId: String): Future[Option[(Rate, Movie, User)]] = db.run {
    _rate.filter(_.id === rateId).join(_movie).on(_.movie === _.id).join(_user).on(_._1.user === _.id).map {
      case ((rate, movie), user) => (rate, movie, user)
    }.result.headOption
  }

  def getForMovie(movieId: String): Future[Seq[(Rate, User)]] = db.run {
    _rate.filter(_.movie === movieId).join(_user).on(_.user === _.id).result
  }

  def create(value: Int, userId: String, movieId: String) = db.run {
    val newId: String = UUID.randomUUID().toString()
    _rate.filter(_.user === userId).filter(_.movie === movieId).result.headOption.flatMap {
      case Some(r) => _rate.insertOrUpdate(Rate(r.id, value, userId, movieId));
      case None => _rate.insertOrUpdate(Rate(newId, value, userId, movieId));
    }
  }

  def delete(rateId: String) = db.run {
    _rate.filter(_.id === rateId).delete
  }

  def update(rateId: String, value: Int, userId: String, movieId: String) = db.run {
    _rate.filter(_.id === rateId).update(Rate(rateId, value, userId, movieId))
  }


}