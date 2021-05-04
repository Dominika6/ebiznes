package models

import play.api.db.slick.DatabaseConfigProvider
import javax.inject.{Inject, Singleton}
import slick.jdbc.JdbcProfile

@Singleton
class MovieToOrderRepository  @Inject() (dbConfigProvider: DatabaseConfigProvider, movieRepository: MovieRepository, orderRepository: OrderRepository) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class OrderItemTable(tag: Tag) extends Table[MovieToOrder](tag, "movieToOrder") {
    val movie = TableQuery[movieRepository.MovieTable]
    val order = TableQuery[orderRepository.OrderTable]

    def orderId = column[String]("orderId")
    def orderId_fk = foreignKey("orderId_fk", orderId, order)(_.orderId, onUpdate = ForeignKeyAction.NoAction , onDelete = ForeignKeyAction.Cascade)
    def movieId = column[String]("movieId")
    def movieId_fk = foreignKey("movieId_fk", movieId, movie)(_.movieId, onUpdate = ForeignKeyAction.NoAction , onDelete = ForeignKeyAction.Cascade)
    def price = column[Double]("price")
    def pk = primaryKey("primaryKey", (orderId, movieId))
    def * = (movieId, price, orderId) <> ((MovieToOrder.apply _).tupled, MovieToOrder.unapply)
  }

}
