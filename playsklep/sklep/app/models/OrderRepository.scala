package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

@Singleton
class OrderRepository @Inject()(payRepository: PayRepository, userRepository: UserRepository, dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class OrderTable(tag: Tag) extends Table[Order](tag, "order") {

    val order = TableQuery[OrderTable]
    val pay = TableQuery[payRepository.PayTable]
    val user = TableQuery[userRepository.UserTable]

    def orderId = column[String]("orderId", O.PrimaryKey)
    def userId = column[String]("userId")
    def userId_fk = foreignKey("userId_fk", userId, user)(_.userId, onUpdate = ForeignKeyAction.NoAction , onDelete = ForeignKeyAction.Cascade)
    def payId = column[String]("payId")
    def payId_fk = foreignKey("payId_fk", payId, pay)(_.payId, onUpdate = ForeignKeyAction.NoAction , onDelete = ForeignKeyAction.Cascade)
    def * = (orderId, userId, payId) <> ((Order.apply _).tupled, Order.unapply)
  }

  def getAllWithPayAndUser(): Future[Seq[(Order, Pay, User, Double)]] = db.run {
    _order.join(_pay).on(_.pay === _.id).join(_user).on(_._1.user === _.id).map {
      case ((order, pay), user) => {
        val value = _orderItems.filter(_.order === order.id).map(_.price).sum.getOrElse(0.0)
        (order, pay, user, value)
      }
    }.result
  }

  def getForUser(userId: String) = db.run {
    _order
      .filter(_.user === userId)
      .join(_pay).on(_.pay === _.id)
      .result
  }

  def getById(orderId: String): Future[Option[Order]] = db.run {
    _order.filter(_.id === orderId).result.headOption
  }

  def getOrderItemsWithMovie(orderId: String): Future[Seq[(OrderItem, Movie)]] = db.run {
    _orderItems.filter(_.order === orderId).join(_movie).on(_.movie === _.id).map {
      case (oi, m) => (oi, m)
    }.result
  }

  def getOrderValue(orderId: String): Future[Double] = db.run {
    _orderItems.filter(_.order === orderId).map(_.price).sum.result.map(_.getOrElse(0.0))
  }

  def getOrderItemsForOrder(orderId: String): Future[Seq[OrderItem]] = db.run {
    _orderItems.filter(_.order === orderId).result
  }

  def getMoviesForOrder(orderId: String): Future[Seq[Movie]] = db.run {
    _orderItems.filter(_.order === orderId).join(_movie).on(_.movie === _.id).map {
      case (oi, m) => (m)
    }.result
  }

  def getByIdWithUserAndPay(orderId: String): Future[Option[(Order, Pay, User)]] = db.run {
    _order.filter(_.id === orderId).join(_pay).on(_.pay === _.id).join(_user).on(_._1.user === _.id).map {
      case ((order, pay), user) => (order, pay, user)
    }.result.headOption
  }

  def create(user: String, pay: String, movies: Seq[String]) = {
    val id: String = UUID.randomUUID().toString()
    val createOrder = _order.insertOrUpdate(Order(id, user, pay))

    var orderItems: Seq[OrderItem] = Seq()
    for (m <- movies) {
      val movie: Option[Movie] = Await.result(movieRepository.getById(m), Duration.Inf)
      orderItems = orderItems :+ OrderItem(id, movie.get.id, movie.get.price)
    }
    val createOrderItems = _orderItems ++= orderItems

    db.run(DBIO.seq(createOrder, createOrderItems).transactionally)
  }

  def delete(orderId: String) = {
    val deleteOrder = _order.filter(_.id === orderId).delete
    val deleteOrderItems = _orderItems.filter(_.order === orderId).delete
    db.run(DBIO.seq(deleteOrder, deleteOrderItems).transactionally)
  }

  def update(orderId: String, user: String, pay: String, movies: Seq[String]) = {
    val updateOrder = _order.filter(_.id === orderId).update(Order(orderId, user, pay))

    var orderItems: Seq[OrderItem] = Seq()
    for (m <- movies) {
      val movie: Option[Movie] = Await.result(movieRepository.getById(m), Duration.Inf)
      orderItems = orderItems :+ OrderItem(orderId, movie.get.id, movie.get.price)
    }
    val cleanOrderItems = _orderItems.filter(_.order === orderId).delete
    val createOrderItems = _orderItems ++= orderItems

    db.run(DBIO.seq(updateOrder, cleanOrderItems, createOrderItems).transactionally)
  }



}