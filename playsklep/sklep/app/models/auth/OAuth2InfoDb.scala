package models.auth

import play.api.libs.json.{Json, OFormat}
import slick.jdbc.SQLiteProfile.api._

case class OAuth2InfoDb(id: String,
                        accessToken: String,
                        tokenType: Option[String],
                        expiresIn: Option[Int],
                        refreshToken: Option[String],
                        loginInfoId: String)

class OAuth2InfoTable(tag: Tag) extends Table[OAuth2InfoDb](tag, "OAuth2Info") {
  def id = column[String]("id", O.PrimaryKey, O.Unique)
  def accessToken = column[String]("accessToken")
  def tokenType = column[Option[String]]("tokenType")
  def expiresIn = column[Option[Int]]("expiresIn")
  def refreshToken = column[Option[String]]("refreshToken")
  def loginInfoId = column[String]("loginInfoId")

  def * =  (id, accessToken, tokenType, expiresIn, refreshToken, loginInfoId) <> ((OAuth2InfoDb.apply _).tupled, OAuth2InfoDb.unapply)
}

object OAuth2InfoDb {
  implicit val oauth2InfoFormat: OFormat[OAuth2InfoDb] = Json.format[OAuth2InfoDb]
}


