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

  class ActorTable(tag: Tag) extends Table[Actor](tag, "actor") {
    def actorId = column[String]("actorId", O.PrimaryKey, O.AutoInc)
    def firstName = column[String]("firstName")
    def surname = column[String]("surname")
    def * = (actorId, firstName, surname) <> ((Actor.apply _).tupled, Actor.unapply)
  }

  private val actor = TableQuery[ActorTable]

  def create(firstName: String, surname: String): Future[Int] = db.run {
    val actorId: String = UUID.randomUUID().toString
    actor.insertOrUpdate(Actor(actorId, firstName, surname))
  }

  def getAll: Future[Seq[Actor]] = db.run {
    actor.result
  }

  def getById(actorId: String): Future[Actor] = db.run {
    actor.filter(_.actorId === actorId).result.head
  }

  def getByIdOption(actorId: String): Future[Option[Actor]] = db.run {
    actor.filter(_.actorId === actorId).result.headOption
  }

  def update(actorId: String, firstName: String, surname: String): Future[Int] = db.run {
    actor.filter(_.actorId === actorId).update(Actor(actorId, firstName, surname))
  }

  def delete(actorId: String): Future[Int] = db.run {
    actor.filter(_.actorId === actorId).delete
  }

}