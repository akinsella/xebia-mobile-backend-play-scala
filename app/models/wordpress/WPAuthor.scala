package models.wordpress

import play.api.libs.json._
import play.api.libs.json.JsObject
import play.api.libs.json.JsNumber

case class WPAuthor(
                    id:Long, slug:String, name:String, firstname:String,
                    lastname:String, nickname:String, url:String, description:String) {

}

object WPAuthor {

  implicit object WPAuthorFormat extends Format[WPAuthor] {
    def reads(json: JsValue): WPAuthor = WPAuthor(
      (json \ "id").as[Long],
      (json \ "slug").as[String],
      (json \ "name").as[String],
      (json \ "first_name").as[String],
      (json \ "last_name").as[String],
      (json \ "nickname").as[String],
      (json \ "url").as[String],
      (json \ "description").as[String]
    )

    def writes(author: WPAuthor): JsValue = JsObject(Seq(
      "id" -> JsNumber(author.id),
      "slug" -> JsString(author.slug),
      "name" -> JsString(author.name),
      "firstname" -> JsString(author.firstname),
      "lastname" -> JsString(author.lastname),
      "nickname" -> JsString(author.nickname),
      "url" -> JsString(author.url),
      "description" -> JsString(author.description)
    ))
  }

}
