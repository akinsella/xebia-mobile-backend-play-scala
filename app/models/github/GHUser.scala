package models.github

import play.api.libs.json._
import play.api.libs.json.JsObject
import play.api.libs.json.JsString
import play.api.libs.json.JsNumber

case class GHUser(
                   id: Long, name: String, login: String, location: String, html_url: String,
                   followers: Long, following: Long, public_repos: Long, public_gists: Long,
                   company: String, _type: String, blog: String, email: String,
                   created_at: String, avatar_url:String
                  ) {

}

object GHUser {

  implicit object GHUserFormat extends Format[GHUser] {
    def reads(json: JsValue): GHUser = GHUser(
      (json \ "id").as[Long],
      (json \ "name").as[String],
      (json \ "login").as[String],
      (json \ "location").as[String],
      (json \ "html_url").as[String],
      (json \ "followers").as[Long],
      (json \ "following").as[Long],
      (json \ "public_repos").as[Long],
      (json \ "public_gists").as[Long],
      (json \ "company").as[String],
      (json \ "type").as[String],
      (json \ "blog").as[String],
      (json \ "email").as[String],
      (json \ "created_at").as[String],
      (json \ "avatar_url").as[String]
    )

    def writes(user: GHUser): JsValue = JsObject(Seq(
      "id" -> JsNumber(user.id),
      "name" -> JsString(user.name),
      "login" -> JsString(user.login),
      "location" -> JsString(user.location),
      "html_url" -> JsString(user.html_url),
      /*"owner" -> JsObject(user.owner),*/
      "followers" -> JsNumber(user.followers),
      "following" -> JsNumber(user.following),
      "public_repos" -> JsNumber(user.public_repos),
      "public_gists" -> JsNumber(user.public_gists),
      "company" -> JsString(user.company),
      "type" -> JsString(user._type),
      "blog" -> JsString(user.blog),
      "email" -> JsString(user.email),
      "created_at" -> JsString(user.created_at),
      "avatar_url" -> JsString(user.avatar_url)
    ))
  }

}
