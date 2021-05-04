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
}