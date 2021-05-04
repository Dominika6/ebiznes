package models

import play.api.db.slick.DatabaseConfigProvider

import javax.inject.{Inject, Singleton}
import slick.jdbc.JdbcProfile


@Singleton
class MovieAndActorRepository @Inject() (dbConfigProvider: DatabaseConfigProvider, movieRepository: MovieRepository, actorRepository: ActorRepository) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class MovieAndActorTable(tag: Tag) extends Table[MovieAndActor](tag, "movieAndActor") {
    val movie = TableQuery[movieRepository.MovieTable]
    val actor = TableQuery[actorRepository.ActorTable]

    def movieId = column[String]("movieId")
    def movieId_fk = foreignKey("movieId_fk", movieId, movie)(_.movieId, onUpdate = ForeignKeyAction.NoAction , onDelete = ForeignKeyAction.Cascade)
    def actorId = column[String]("actorId")
    def actorId_fk = foreignKey("actorId_fk", actorId, actor)(_.actorId, onUpdate = ForeignKeyAction.NoAction , onDelete = ForeignKeyAction.Cascade)
    def pk = primaryKey("primaryKey", (movieId, actorId))
    def * = (movieId, actorId) <> ((MovieAndActor.apply _).tupled, MovieAndActor.unapply)
  }

}
