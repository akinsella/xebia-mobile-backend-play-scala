package models.github

import play.api.libs.json._
import play.api.libs.json.JsObject
import play.api.libs.json.JsString
import play.api.libs.json.JsNumber

case class GHOwner(
                   id: Long, login: String, gravatar_id: String, avatar_url:String
                   ) {

}

object GHOwner {

  implicit object GHOwnerFormat extends Format[GHOwner] {
    def reads(json: JsValue): GHOwner = GHOwner(
      (json \ "id").as[Long],
      (json \ "login").as[String],
      (json \ "gravatar_id").asOpt[String].getOrElse(""),
      (json \ "avatar_url").asOpt[String].getOrElse("")
    )

    def writes(owner: GHOwner): JsValue = JsObject(Seq(
      "id" -> JsNumber(owner.id),
      "login" -> JsString(owner.login),
      "gravatar_id" -> JsString(owner.gravatar_id),
      "avatar_url" -> JsString(owner.avatar_url)
    ))
  }

}
