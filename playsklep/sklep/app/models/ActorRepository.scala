package models
import java.util.UUID
import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ActorRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  val actor = TableQuery[ActorTable]
  val movieActors = TableQuery[MovieAndActorTable]

  def create(firstName: String, surname: String): Future[Int] = db.run {
    val actorId: String = UUID.randomUUID().toString
    actor.insertOrUpdate(Actor(actorId, firstName, surname))
  }

  def getAll(): Future[Seq[Actor]] = db.run {
    actor.result
  }

  def isExist(actorId: String): Future[Boolean] = db.run {
    actor.filter(_.actorId === actorId).exists.result
  }

  def getForMovie(movieId: String): Future[Seq[Actor]] = db.run {
    movieActors.filter(_.movieId === movieId).join(actor).on(_.actorId === _.actorId).map {
      case (ma, a) => a
    }.result
  }

  def getById(actorId: String): Future[Option[Actor]] = db.run {
    actor.filter(_.actorId === actorId).result.headOption
  }

  def update(actorId: String, firstName: String, surname: String): Future[Int] = db.run {
    actor.filter(_.actorId === actorId).update(Actor(actorId, firstName, surname))
  }

  def delete(actorId: String): Future[Int] = db.run {
    actor.filter(_.actorId === actorId).delete
  }
}