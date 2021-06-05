package models.auth

import play.api.libs.json.{Json, OFormat}
import slick.jdbc.SQLiteProfile.api._

case class UserLoginInfo(userId: String, loginInfoId: String)

class UserLoginInfoTable(tag: Tag) extends Table[UserLoginInfo](tag, "user_login_info") {
  def userId = column[String]("userId")
  def loginInfoId = column[String]("loginInfoId")

  override def * = (userId, loginInfoId) <> ((UserLoginInfo.apply _).tupled, UserLoginInfo.unapply)
}

object UserLoginInfo {
  implicit val userLoginInfoFormat: OFormat[UserLoginInfo] = Json.format[UserLoginInfo]
}