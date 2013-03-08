package models.wordpress.stripped

import play.api.libs.json._
import play.api.libs.json.JsObject
import play.api.libs.json.JsNumber

case class WPAuthor(
                    id:Long, slug:String, name:String, firstname:String,
                    lastname:String, nickname:String)

object WPAuthor {

  implicit object WPAuthorFormat extends Format[WPAuthor] {
    def reads(json: JsValue) = JsSuccess(WPAuthor(
      (json \ "id").as[Long],
      (json \ "slug").as[String],
      (json \ "name").as[String],
      (json \ "first_name").as[String],
      (json \ "last_name").as[String],
      (json \ "nickname").as[String]
    ))

    def writes(author: WPAuthor): JsValue = JsObject(Seq(
      "id" -> JsNumber(author.id),
      "slug" -> JsString(author.slug),
      "name" -> JsString(author.name),
      "firstname" -> JsString(author.firstname),
      "lastname" -> JsString(author.lastname),
      "nickname" -> JsString(author.nickname)
    ))
  }

}
