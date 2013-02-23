package models.twitter

import play.api.libs.json._


case class TwitterUser(login: String, email: String, avatar_url: String, name: String)


object TwitterUser {

  implicit def TwitterUserReacds: Reads[TwitterUser] = new Reads[TwitterUser] {
    def reads(json: JsValue) =
      JsSuccess(TwitterUser(
        (json \ "login").as[String],
        (json \ "email").as[String],
        (json \ "avatar_url").as[String],
        (json \ "name").as[String]
      ))
  }

}
