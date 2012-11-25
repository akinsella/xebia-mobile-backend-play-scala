package models.wordpress

import play.api.libs.json._
import play.api.libs.json.JsObject
import play.api.libs.json.JsNumber

case class WPTag(id:Long, slug:String, title:String, description:String, postCount:Long) {

}

object WPTag {

  implicit object WPTagFormat extends Format[WPTag] {
    def reads(json: JsValue): WPTag = WPTag(
      (json \ "id").as[Long],
      (json \ "slug").as[String],
      (json \ "title").as[String],
      (json \ "description").as[String],
      (json \ "post_count").as[Long]
    )

    def writes(tag: WPTag): JsValue = JsObject(Seq(
      "id" -> JsNumber(tag.id),
      "slug" -> JsString(tag.slug),
      "title" -> JsString(tag.title),
      "description" -> JsString(tag.description),
      "postCount" -> JsNumber(tag.postCount)
    ))
  }

}
