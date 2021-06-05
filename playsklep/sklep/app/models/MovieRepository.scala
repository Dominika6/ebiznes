package models
import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class MovieRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  val movie = TableQuery[MovieTable]
  val movieAndFilmtype = TableQuery[MovieAndFilmtypeTable]
  val movieAndActor = TableQuery[MovieAndActorTable]
  val movieAndDirectors = TableQuery[MovieAndDirectorTable]
  val order = TableQuery[OrderTable]
  val movieToOrder = TableQuery[OrderItemTable]

  def getAll: Future[Seq[Movie]] = db.run {
    movie.result
  }

  def getById(movieId: String): Future[Option[Movie]] = db.run {
    movie.filter(_.movieId === movieId).result.headOption
  }

  def isExist(movieId: String): Future[Boolean] = db.run {
    movie.filter(_.movieId === movieId).exists.result
  }

  def getForFilmtype(filmtypeId: String): Future[Seq[Movie]] = db.run {
    movieAndFilmtype.filter(_.filmtypeId === filmtypeId).join(movie).on(_.movieId === _.movieId).map {
      case (mg, m) => m
    }.result
  }

  def getForActor(actorId: String): Future[Seq[Movie]] = db.run {
    movieAndActor.filter(_.actorId === actorId).join(movie).on(_.movieId === _.movieId).map {
      case (ma, b) => b
    }.result
  }

  def getForDirector(directorId: String): Future[Seq[Movie]] = db.run {
    movieAndDirectors.filter(_.directorId === directorId).join(movie).on(_.movieId === _.movieId).map {
      case (md, m) => m
    }.result
  }

  def getForUser(userId: String): Future[Seq[Movie]] = db.run {
    order
      .filter(_.userId === userId)
      .join(movieToOrder).on(_.orderId === _.orderId)
      .join(movie).on(_._2.movieId === _.movieId)
      .map {
        case ((o, oi), m) => m
      }.distinct.result
  }


  def create(title: String, publicationDate: String, price: Double, details: String, actors: Seq[String], directors: Seq[String], filmtypes: Seq[String]): Future[Unit] = {

    val id: String = UUID.randomUUID().toString
    val createMovie = movie.insertOrUpdate(Movie(id, title, publicationDate, price, details ))

    var actorSeq: Seq[MovieAndActor] = Seq()
    for (a <- actors) {
      actorSeq = actorSeq :+ MovieAndActor(id, a)
    }
    val bindActors = movieAndActor ++= actorSeq

    var directorSeq: Seq[MovieAndDirector] = Seq()
    for (d <- directors) {
      directorSeq = directorSeq :+ MovieAndDirector(id, d)
    }
    val bindDirectors = movieAndDirectors ++= directorSeq

    var genreSeq: Seq[MovieAndFilmtype] = Seq()
    for (g <- filmtypes) {
      genreSeq = genreSeq :+ MovieAndFilmtype(id, g)
    }
    val bindGenres = movieAndFilmtype ++= genreSeq

    db.run(DBIO.seq(createMovie, bindActors, bindDirectors, bindGenres).transactionally)
  }

  def delete(movieId: String): Future[Unit] = {
    val deleteMovie = movie.filter(_.movieId === movieId).delete
    db.run(DBIO.seq(deleteMovie).transactionally)
  }

  def update(movieId: String, title: String, publicationDate: String, price: Double , details: String, actors: Seq[String], directors: Seq[String], filmtypes: Seq[String]): Future[Unit] = {

    val updateMovie = movie.filter(_.movieId === movieId).update(Movie(movieId, title, publicationDate, price, details))


    var actorSeq: Seq[MovieAndActor] = Seq()
    for (a <- actors) {
      actorSeq = actorSeq :+ MovieAndActor(movieId, a)
    }
    val cleanActors = movieAndActor.filter(_.movieId === movieId).delete
    val bindActors = movieAndActor ++= actorSeq

    var directorSeq: Seq[MovieAndDirector] = Seq()
    for (d <- directors) {
      directorSeq = directorSeq :+ MovieAndDirector(movieId, d)
    }
    val cleanDirectors = movieAndDirectors.filter(_.movieId === movieId).delete
    val bindDirectors = movieAndDirectors ++= directorSeq

    var genreSeq: Seq[MovieAndFilmtype] = Seq()
    for (g <- filmtypes) {
      genreSeq = genreSeq :+ MovieAndFilmtype(movieId, g)
    }
    val cleanGenres = movieAndFilmtype.filter(_.movieId === movieId).delete
    val bindGenres = movieAndFilmtype ++= genreSeq

    db.run(DBIO.seq(
      updateMovie,
      cleanActors, bindActors,
      cleanDirectors, bindDirectors,
      cleanGenres, bindGenres
    ).transactionally)
  }
}