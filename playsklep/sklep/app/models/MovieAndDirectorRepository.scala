package models

import play.api.db.slick.DatabaseConfigProvider
import javax.inject.{Inject, Singleton}
import slick.jdbc.JdbcProfile

@Singleton
class MovieAndDirectorRepository  @Inject() (dbConfigProvider: DatabaseConfigProvider, movieRepository: MovieRepository, directorRepository: DirectorRepository) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class MovieAndDirectorTable(tag: Tag) extends Table[MovieAndDirector](tag, "movieAndDirector") {

    val movie = TableQuery[movieRepository.MovieTable]
    val director = TableQuery[directorRepository.DirectorTable]

    def movieId = column[String]("movieId")
    def movieId_fk = foreignKey("movieId_fk", movieId, movie)(_.movieId, onUpdate = ForeignKeyAction.NoAction , onDelete = ForeignKeyAction.Cascade)
    def directorId = column[String]("directorId")
    def directorId_fk = foreignKey("directorId_fk", directorId, director)(_.directorId, onUpdate = ForeignKeyAction.NoAction , onDelete = ForeignKeyAction.Cascade)
    def pk = primaryKey("primaryKey", (movieId, directorId))
    def * = (movieId, directorId) <> ((MovieAndDirector.apply _).tupled, MovieAndDirector.unapply)
  }


}
