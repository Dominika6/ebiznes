package models
import models.auth.{User, UserTable}
import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import java.util.UUID
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

@Singleton
class OrderRepository @Inject()(movieRepository: MovieRepository,
                                dbConfigProvider: DatabaseConfigProvider
                               )(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  val order = TableQuery[OrderTable]
  val pay = TableQuery[PayTable]
  val user = TableQuery[UserTable]
  val _orderItems = TableQuery[OrderItemTable]
  val movie = TableQuery[MovieTable]


  def getAllWithPayAndUser: Future[Seq[(Order, Pay, User, Double)]] = db.run {
    order.join(pay).on(_.payId === _.payId).join(user).on(_._1.userId === _.userId).map {
      case ((order, pay), user) =>
        val value = _orderItems.filter(_.orderId === order.orderId).map(_.price).sum.getOrElse(0.0)
        (order, pay, user, value)
    }.result
  }

  def getForUser(userId: String): Future[Seq[(Order, Pay)]] = db.run {
    order
      .filter(_.userId === userId)
      .join(pay).on(_.payId === _.payId)
      .result
  }

  def getById(orderId: String): Future[Option[Order]] = db.run {
    order.filter(_.orderId === orderId).result.headOption
  }

  def getOrderItemsWithMovie(orderId: String): Future[Seq[(MovieToOrder, Movie)]] = db.run {
    _orderItems.filter(_.orderId === orderId).join(movie).on(_.movieId === _.movieId).map {
      case (oi, m) => (oi, m)
    }.result
  }

  def getOrderValue(orderId: String): Future[Double] = db.run {
    _orderItems.filter(_.orderId === orderId).map(_.price).sum.result.map(_.getOrElse(0.0))
  }

  def getOrderItemsForOrder(orderId: String): Future[Seq[MovieToOrder]] = db.run {
    _orderItems.filter(_.orderId === orderId).result
  }

  def getMoviesForOrder(orderId: String): Future[Seq[Movie]] = db.run {
    _orderItems.filter(_.orderId === orderId).join(movie).on(_.movieId === _.movieId).map {
      case (oi, m) => (m)
    }.result
  }

  def getByIdWithUserAndPay(orderId: String): Future[Option[(Order, Pay, User)]] = db.run {
    order.filter(_.orderId === orderId).join(pay).on(_.payId === _.payId).join(user).on(_._1.userId === _.userId).map {
      case ((order, pay), user) => (order, pay, user)
    }.result.headOption
  }

  def create(user: String, pay: String, movies: Seq[String]): Future[Unit] = {
    val id: String = UUID.randomUUID().toString
    val createOrder = order.insertOrUpdate(Order(id, user, pay))

    var orderItems: Seq[MovieToOrder] = Seq()
    for (m <- movies) {
      val movie: Option[Movie] = Await.result(movieRepository.getById(m), Duration.Inf)
      orderItems = orderItems :+ MovieToOrder(id, movie.get.price, movie.get.movieId)
    }
    val createOrderItems = _orderItems ++= orderItems

    db.run(DBIO.seq(createOrder, createOrderItems).transactionally)
  }

  def delete(orderId: String): Future[Unit] = {
    val deleteOrder = order.filter(_.orderId === orderId).delete
    val deleteOrderItems = _orderItems.filter(_.orderId === orderId).delete
    db.run(DBIO.seq(deleteOrder, deleteOrderItems).transactionally)
  }

  def update(orderId: String, user: String, pay: String, movies: Seq[String]): Future[Unit] = {
    val updateOrder = order.filter(_.orderId === orderId).update(Order(orderId, user, pay))

    var orderItems: Seq[MovieToOrder] = Seq()
    for (m <- movies) {
      val movie: Option[Movie] = Await.result(movieRepository.getById(m), Duration.Inf)
      orderItems = orderItems :+ MovieToOrder(orderId, movie.get.price, movie.get.movieId)
    }
    val cleanOrderItems = _orderItems.filter(_.orderId === orderId).delete
    val createOrderItems = _orderItems ++= orderItems

    db.run(DBIO.seq(updateOrder, cleanOrderItems, createOrderItems).transactionally)
  }
}