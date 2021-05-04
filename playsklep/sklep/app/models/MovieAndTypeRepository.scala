package models

import play.api.db.slick.DatabaseConfigProvider
import javax.inject.{Inject, Singleton}
import slick.jdbc.JdbcProfile

@Singleton
class MovieAndTypeRepository @Inject() (dbConfigProvider: DatabaseConfigProvider, typeRepository: TypeRepository, movieRepository: MovieRepository) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class MovieAndTypeTable(tag: Tag) extends Table[MovieAndType](tag, "movieAndTypeTable") {

    val movie = TableQuery[movieRepository.MovieTable]
    val movieType = TableQuery[typeRepository.TypeTable]

    def movieId = column[String]("movieId")
    def movie_fk = foreignKey("movie_fk", movieId, movie)(_.movieId, onUpdate = ForeignKeyAction.NoAction , onDelete = ForeignKeyAction.Cascade)
    def typeId = column[String]("typeId")
    def type_fk = foreignKey("type_fk", typeId, movieType)(_.typeId, onUpdate = ForeignKeyAction.NoAction , onDelete = ForeignKeyAction.Cascade)
    def pk = primaryKey("primaryKey", (movieId, typeId))
    def * = (movieId, typeId) <> ((MovieAndType.apply _).tupled, MovieAndType.unapply)
  }

}
